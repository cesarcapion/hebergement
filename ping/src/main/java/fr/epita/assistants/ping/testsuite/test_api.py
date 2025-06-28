import requests
from termcolor import cprint
import pytest


BASE_URL = "http://localhost:8080"

def pretty_print(success: bool, message: str):
    if success:
        cprint(f"[SUCCESS] {message}", "green")
    else:
        cprint(f"[FAILURE] {message}", "red")
tokenADMIN= "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI4ODc5ZmYyNi1lOTY3LTRlM2MtYjJmZS1lNzY1NDliNDAyY2MiLCJncm91cHMiOlsiYWRtaW4iXSwiaXNzIjoiaHR0cDovL21vbi1hcHAuZXBpdGEuZnIiLCJpYXQiOjE3NTEwMzQzODIsImV4cCI6MTc1MTAzNzk4MiwianRpIjoiZmIzYzc0MmItNzYyOC00YmQ0LWEzOGItNjQyYTUxYTE2NTliIn0.COnttR3thQ7rmgNcbdS_BXRMtRbqZzET0aF3CXaizP7dSH9woyxlks0CTPS7HPP61d4kcgDlXtmlsyamiU0dOfjqwpxhMVeVIvwChNrctAwI_ucfOPIKto_4l0j7I-nv0f1wZPJb5jiXu4Jv29FbH6W9o8UGGEu3ISlYJkKCtDgi6AKrXvFL1DVMYCsJsd3oVTObxhY0ZDW9GlSAI709O7WUVgbvj96hloy4wjpFVy-DiG8uumJpR6g7BMR8G6alrlg-mFGp4XXStr3GcEjKNXVXDOEk5Yge8Rb6wWh81fsc3lIcHPLLyNSI3H2B34Si2vi5-0WxfWGNx3FM3xC-qQ"
tokenUSER= "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJlNDBjZDNiNi1iZjAxLTQ0MDItODgwNC00NDQxMzVhZjlmZjEiLCJncm91cHMiOlsidXNlciJdLCJpc3MiOiJodHRwOi8vbW9uLWFwcC5lcGl0YS5mciIsImlhdCI6MTc1MTAzNDcwMywiZXhwIjoxNzUxMDM4MzAzLCJqdGkiOiIxMWRhYWQwMy0zODFmLTRmZGMtODY4Yy1mOGZhMzc1NGFhNmEifQ.HcTd185HQPItnPXlTYyd2HqIStj-PXL79a3yZ75D29z-5rN8jIJA8mZdrHEL5YpksVTWFfDwgSt5MBewuIMgpu4ffTyGJMYwca9aZfYvP4SzeMyJ5g6CB8F97GtAAIn9cJ9JzFs-zoGbjpjSuiMp27dSfz8mQdByRpul_OHl0nrjs7-8KgYXPdIHtKz7yWAv4HBdoRUIKg7Uw8ZY1cFLfumSBR-eSk3TylrrAAlI2FgGVkBdsvBXyLWkZs7ICw1hFT1zqZ70GNqmWZ22LyYc4U1l2hBvYAArsSK9f_mtbS0CGpGrqn-HV_u_eTd43-ARuprrWKhb_jwVgOCyubXBKg"
johndoeID = None
def test_create_user_success():
    url = f"{BASE_URL}/api/user/new-account"
    payload = {
        "mail": "john.doe@epita.fr",
        "password": "StrongPass123!",
        "isAdmin": False
    }

    response = requests.post(url, json=payload)
    data = response.json()

    pretty_print(response.status_code == 200, f"Create user response: {data}")

    assert response.status_code == 200, "Expected 200 OK for valid user creation"
    assert "id" in data
    assert data["mail"] == payload["mail"]
def test_create_user_conflict():
    url = f"{BASE_URL}/api/user/new-account"
    payload = {
        "mail": "remy.mazri@epita.fr",  # Already exists
        "password": "StrongPass123!",
        "isAdmin": False
    }

    response = requests.post(url, json=payload)
    data = response.json()

    pretty_print(response.status_code == 409, f"Conflict response: {data}")

    assert response.status_code == 409, "Expected 409 Conflict for duplicate email"
    assert data.get("message") == "The mail is already taken"
def test_create_user_invalid_login():
    url = f"{BASE_URL}/api/user/new-account"
    payload = {
        "mail": "invalidemail",
        "password": "short",
        "isAdmin": True
    }

    response = requests.post(url, json=payload)
    data = response.json()

    pretty_print(response.status_code == 400, f"Invalid login response: {data}")

    assert response.status_code == 400, "Expected 400 Bad Request for invalid email"
    assert "message" in data


def test_get_users():
    global johndoeID

    token = tokenADMIN
    headers = {"Authorization": f"Bearer {token}"}

    response = requests.get(f"{BASE_URL}/api/user/all", headers=headers)
    assert response.status_code == 200, "Expected 200 OK for getting all users"

    data = response.json()
    assert isinstance(data, list), "Expected a list of users"

    johndoeID = next((user["id"] for user in data if user["mail"] == "john.doe@epita.fr"), None)
    print("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + johndoeID)
    assert johndoeID is not None, "john.doe@epita.fr not found in user list"

def test_get_users_from_user():
    global johndoeID

    token = tokenUSER
    headers = {"Authorization": f"Bearer {token}"}

    response = requests.get(f"{BASE_URL}/api/user/all", headers=headers)
    assert response.status_code == 403, "Expected 403 OK for getting all users"

def test_get_users_from_not_authorized():
    global johndoeID

    token = tokenUSER
    headers = {"Authorization": f"Bearer "}

    response = requests.get(f"{BASE_URL}/api/user/all", headers=headers)
    assert response.status_code == 401, "Expected 401 OK for getting all users"

def test_get_user():
    global johndoeID
    assert johndoeID is not None, "john.doe@epita.fr must be created before deletion"

    url = f"{BASE_URL}/api/user/{johndoeID}"
    headers = {"Authorization": f"Bearer {tokenADMIN}"}

    response = requests.get(url, headers=headers)
    pretty_print(response.status_code == 200, f"Get user response: {response.text}")

    assert response.status_code == 200, "Expected 200 OK for successful get"

def test_get_user_from_user():
    global johndoeID
    assert johndoeID is not None, "john.doe@epita.fr must be created before deletion"

    url = f"{BASE_URL}/api/user/{johndoeID}"
    headers = {"Authorization": f"Bearer {tokenUSER}"}

    response = requests.get(url, headers=headers)
    pretty_print(response.status_code == 403, f"Get user response: {response.text}")

    assert response.status_code == 403, "Expected 403"

def test_get_user_from_not_authorized():
    global johndoeID
    assert johndoeID is not None, "john.doe@epita.fr must be created before deletion"

    url = f"{BASE_URL}/api/user/{johndoeID}"
    headers = {"Authorization": f"Bearer "}

    response = requests.get(url, headers=headers)
    pretty_print(response.status_code == 401, f"Get user response: {response.text}")

    assert response.status_code == 401, "Expected 401"


def test_get_user_from_user():
    global johndoeID
    assert johndoeID is not None, "john.doe@epita.fr must be created before deletion"

    url = f"{BASE_URL}/api/user/{johndoeID}"
    headers = {"Authorization": f"Bearer {tokenUSER}"}

    response = requests.get(url, headers=headers)
    pretty_print(response.status_code == 403, f"Get user response: {response.text}")

    assert response.status_code == 403, "Expected 403"

def test_update_user():
    global johndoeID
    assert johndoeID is not None, "john.doe@epita.fr must be created before deletion"
    payload = {
        "password": "Onsenfou(-_151651",
        "displayName": "Name",
        "avatar": ""
    }
    url = f"{BASE_URL}/api/user/{johndoeID}"
    headers = {"Authorization": f"Bearer {tokenADMIN}"}

    response = requests.put(url, headers=headers,json=payload)
    pretty_print(response.status_code == 200, f"Get user response: {response.text}")

    assert response.status_code == 200, "Expected 200"

def test_delete_user_fom_user():
    global johndoeID
    assert johndoeID is not None, "john.doe@epita.fr must be created before deletion"

    url = f"{BASE_URL}/api/user/{johndoeID}"
    headers = {"Authorization": f"Bearer {tokenUSER}"}

    response = requests.delete(url, headers=headers)
    pretty_print(response.status_code == 403, f"Delete user response: {response.text}")

    assert response.status_code == 403, "Expected 200 OK for successful deletion"
def test_delete_user_from_not_authenticated_user():
    global johndoeID
    assert johndoeID is not None, "john.doe@epita.fr must be created before deletion"

    url = f"{BASE_URL}/api/user/{johndoeID}"
    headers = {"Authorization": f"Bearer "}

    response = requests.delete(url, headers=headers)
    pretty_print(response.status_code == 401, f"Delete user response: {response.text}")

    assert response.status_code == 401, "Expected 200 OK for successful deletion"

def test_delete_user():
    global johndoeID
    assert johndoeID is not None, "john.doe@epita.fr must be created before deletion"

    url = f"{BASE_URL}/api/user/{johndoeID}"
    headers = {"Authorization": f"Bearer {tokenADMIN}"}

    response = requests.delete(url, headers=headers)
    pretty_print(response.status_code == 204, f"Delete user response: {response.text}")

    assert response.status_code == 204, "Expected 200 OK for successful deletion"