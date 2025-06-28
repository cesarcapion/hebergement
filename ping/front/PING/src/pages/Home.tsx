import { Link } from 'react-router-dom';
import './Home.css';

const Home = () => {
  return (
    <>
      <Link className="profile-button" to="/profile" title="My Profile">👤</Link>

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
          <button className="btn">My tickets</button>
          </Link>
      </div>
    </>
  );
};

export default Home;

