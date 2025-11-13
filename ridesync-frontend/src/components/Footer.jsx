import React from "react";

function Footer() {
  return (
    <footer
      className="bg-dark text-white py-3 text-center fixed-bottom shadow-sm"
      style={{ zIndex: 1030 }}
    >
      <div className="container">
        <small>
          &copy; {new Date().getFullYear()} RideSync. All rights reserved | Built with ❤️ using Spring Boot, React, GraphQL, and Bootstrap.
        </small>
      </div>
    </footer>
  );
}

export default Footer;
