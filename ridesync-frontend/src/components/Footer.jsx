
import React from "react";

function Footer() {
  return (
    <footer className="rs-footer">
      <div className="rs-footer-inner">
        <div className="rs-footer-brand">
          <span className="rs-footer-name">RIDESYNC</span>
          <span className="rs-footer-version">V2</span>
        </div>

        <div className="rs-footer-divider" />

        <small className="rs-footer-copyright">
          © {new Date().getFullYear()} RideSync. All rights reserved.
        </small>

        <small className="rs-footer-credit">
          Built with <span className="rs-footer-heart">♥</span> using
          <span> Spring Boot</span>, <span>React</span>,
          <span> REST APIs</span> and <span>Bootstrap</span>.
        </small>
      </div>
    </footer>
  );
}

export default Footer;
