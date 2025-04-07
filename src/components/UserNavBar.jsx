import React from 'react';
import logo from '../assets/logo.png';
import { Link } from 'react-router-dom';

function UserNavBar() {
  return (
    <nav className="bg-white py-4" style={{ borderTop: '4px solid #8A9A5B', borderBottom: '4px solid #8A9A5B' }}>
      <div className="container mx-auto flex items-center justify-between">
        <div className="flex items-center justify-start">
          <img src={logo} alt="FurrEverCare Logo" className="h-18 -ml-35" />
          <span className="font-bold text-xl" style={{ color: '#042C3C', fontFamily: "'Baloo', cursive", fontSize: '1.5rem', padding: "10px"}}>FurrEverCare</span>
        </div>

        <div className="flex space-x-8" style={{ fontSize: '1.5rem', fontFamily: "'Baloo', cursive" }}>
          <Link to="/pawpedia" style={{ color: '#042C3C', textDecoration: 'none', padding: "15px" }} className="hover:text-gray-500">PawPedia</Link>
          <Link to="/mypets" style={{ color: '#042C3C', textDecoration: 'none', padding: "15px"  }} className="hover:text-gray-500">My Pets</Link>
          <Link to="/treatmenttracker" style={{ color: '#042C3C', textDecoration: 'none', padding: "15px"  }} className="hover:text-gray-500">Treatment Tracker</Link>
          <Link to="/wellnesstimeline" style={{ color: '#042C3C', textDecoration: 'none', padding: "15px"  }} className="hover:text-gray-500">Wellness Timeline</Link>
        </div>

        <div>
          {/* User Profile Icon Placeholder */}
          <span
  className="text-4xl rounded-full bg-black"
  style={{
    color: 'white',
    width: '40px',
    height: '40px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center', // Center the icon inside the circle
    position: 'absolute', // Use absolute positioning
    right: '65px', // Move it 100px outside the parent to the right
    transform: 'translateY(-50%)', // Optional: Center vertically if needed
    opacity: 0.7,
  }}
>
  <i className="fas fa-user-circle"></i>
</span>


        </div>
      </div>
    </nav>
  );
}

export default UserNavBar;
