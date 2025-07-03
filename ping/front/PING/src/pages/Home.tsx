import {Link} from 'react-router-dom';
import './Home.css';


const Home = () => {
  /*const navigate = useNavigate();
  const [role, setRole] = useState<Role | null>(null);
  useEffect(() => {
      const token = localStorage.getItem("token");
      fetch("http://localhost:8080/api/roles/2", {
          method: "GET",
          headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`
          },
      })
          .then((res) => {
              if (res.status === 401){
                  navigate("/login");
              }
              return res.json();
          })
          .then((data) => setRole(data))
          .catch((err) => console.error(err));
  }, []);
  if (!role) return <p>Chargement du rôle...</p>;*/
    return (
    <>
      <Link className="profile-button" to="/profile" title="My Profile">👤</Link>
      <div className="flex flex-col items-center w-screen">
        <div className="header-block">
          <div className="text-block">
            <div className="title">Tick-E Taka:</div>
            <div className="subtitle">
              <u>Every question passes,</u><br />
              <u>Every answer scores.</u>
            </div>
          </div>
          <img src="/White-Logo-without-bg.png" alt="Logo" className="logo" />
        </div>

        <div className="button-group">
            <Link to="/qa">
                <button className="btn">Q&amp;A</button>
            </Link>
            <Link to="/my-tickets">
                <button className="btn">My Tickets</button>
            </Link>
        </div>
      </div>
    </>
  );
};

export default Home;

