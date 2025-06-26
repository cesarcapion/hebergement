import pytest
import requests as r
import uuid

URL = "http://localhost:8080/api/user"

PRO_URL = "http://localhost:8080/api/projects"


def uuidd():
    return str(uuid.uuid4())

### USER ### 

# create a user
# 401
def test_create_user_unautorized():
    response = r.post(f"{URL}", json={
        "login" : "unautorized-user",
        "password" : "kk",
        "isAdmin" : False
    })

    assert response.status_code == 401

@pytest.fixture
def non_admin_token():
    login_payload = {
        "login": "user.test",
        "password": "testpass"
    }
    response = r.post(f"{URL}/user/login", json=login_payload)
    assert response.status_code == 200
    return response.json()["token"]

def test_access_admin_endpoint_as_user(non_admin_token):
    headers = {
        "Authorization": f"Bearer {non_admin_token}"
    }

    create_payload = {
        "login": "new.user",
        "password": "newpass",
        "isAdmin": False
    }

    response = r.post(f"{BASE_URL}/user", json=create_payload, headers=headers)

    assert response.status_code == 403
    assert response.json()["message"] == "The user is not an admin"

# list all user
# 401
def test_get_all_users_unauthorized():
    response = r.get(f"{URL}/all")
    assert response.status_code == 401

# login a user
# 400
def test_login_passw_null():
    response = r.post(f"{URL}/login", json = {
        "login" : None,
        "password" : None
    })

    assert response.status_code == 400

# 401
def test_login_passw_comb_is_invalid():
    response = r.post(f"{URL}/login", json={
        "login": "unknown_user",
        "password": "wrongpass"
    })

    assert response.status_code == 401

# refresh a token 
# 401

def test_refresh_token_not_authorized():
    response = r.get(f"{URL}/refresh")
    
    assert response.status_code == 401


# update a user 
# 401

def test_update_user_unauthorized():
    user_id = uuidd()
    payload = {
        "displayName": "KK",
        "password": "KKKKKKKKK",
        "avatar": "https://KK.png"
    }
    response = r.put(f"{URL}/{user_id}", json=payload)
    assert response.status_code == 401

# get user 
# 401

def test_get_user_unauthorized():
    user_id = uuidd() 
    response = r.get(f"{URL}/{user_id}")
    assert response.status_code == 401

# delete user 
# 401

def test_delete_user_unauthorized():
    user_id = uuidd() 
    response = r.delete(f"{URL}/{user_id}")
    assert response.status_code == 401





### PROJECT ###

# List projet of user

# 401
def test_get_projects_unauthorized():
    response = r.get(PRO_URL)
    assert response.status_code == 401

# Create a project 
# 401

def test_create_project_unauthorized():
    response = r.post(PRO_URL, json={
        "name": "Unauthorized Project"
    })

    assert response.status_code == 401

# List of all project 

# 401
def test_get_all_projects_unauthorized():
    response = r.get(PRO_URL)
    assert response.status_code == 401

# Update a Project

# 401
def test_update_project_unauthorized():
    random_id = uuidd()
    response = r.put(f"{PRO_URL}/{random_id}", json={"name": "New name"})
    assert response.status_code == 401

# Get a project

# 401
def test_get_project_unauthorized():
    random_id = uuidd()
    response = r.get(f"{PRO_URL}/{random_id}")
    assert response.status_code == 401

# Delete a Project

# 401
def test_delete_project_unauthorized():
    random_id = uuidd()
    response = r.delete(f"{PRO_URL}/{random_id}")
    assert response.status_code == 401

# Add a member 

# 401
def test_add_user_to_project_unauthorized():
    project_id = uuidd()
    user_id = uuidd()
    response = r.post(f"{PRO_URL}/{project_id}/add-user", json={"userId": user_id})
    assert response.status_code == 401

# Execute a feature

# 401
def test_exec_feature_unauthorized():
    random_id = uuidd()
    response = r.post(f"{PRO_URL}/{random_id}/exec", json={
        "feature": "git",
        "command": "init",
        "params": []
    })
    assert response.status_code == 401

# Remove a member

# 401
def test_remove_user_unauthorized():
    project_id = uuidd()
    user_id = uuidd()
    response = r.post(f"{PRO_URL}/{project_id}/remove-user", json={"userId": user_id})
    assert response.status_code == 401



### FILE ###

# Get a File

# 401
def test_get_file_unauthorized():
    project_id =  uuidd()
    response = r.get(f"{PRO_URL}/{project_id}/files", params={"path": "README.md"})
    assert response.status_code == 401

# Delete a File

# 401
def test_delete_file_unauthorized():
    project_id =  uuidd()
    response = r.delete(f"{PRO_URL}/{project_id}/files", json={"relativePath": "file.txt"})
    assert response.status_code == 401

# Create a File 

# 401
def test_create_file_unauthorized():
    project_id = uuidd()
    response = r.post(f"{PRO_URL}/{project_id}/files", json={"relativePath": "newfile.txt"})
    assert response.status_code == 401

# Move a file

# 401
def test_move_file_unauthorized():
    project_id = uuidd()
    response = r.put(f"{PRO_URL}/{project_id}/files/move", json={
        "src": "old.txt",
        "dst": "new.txt"
    })
    assert response.status_code == 401

# Upload a new file 

# 401
def test_upload_file_unauthorized():
    project_id = uuidd()
    response = r.post(
        f"{PRO_URL}/{project_id}/files/upload",
        params={"path": "upload.txt"},
        data=b"hello world",  # binaire
        headers={"Content-Type": "application/octet-stream"}
    )
    assert response.status_code == 401


### FOLDER ###

# List a folder 

# 401
def test_list_folder_unauthorized():
    project_id = uuidd()
    response = r.get(f"{PRO_URL}/{project_id}/folders", params={"path": ""})
    assert response.status_code == 401

# Delete a Folder 

# 401
def test_delete_folder_unauthorized():
    project_id = uuidd()
    response = r.delete(f"{PRO_URL}/{project_id}/folders", json={"relativePath": "some_folder"})
    assert response.status_code == 401

# Create a folder

# 401
def test_create_folder_unauthorized():
    project_id = uuidd()
    response = r.post(f"{PRO_URL}/{project_id}/folders", json={"relativePath": "docs"})
    assert response.status_code == 401

# Move a folder 

# 401
def test_move_folder_unauthorized():
    project_id = uuidd()
    response = r.put(f"{PRO_URL}/{project_id}/folders/move", json={
        "src": "old_folder",
        "dst": "new_folder"
    })
    assert response.status_code == 401