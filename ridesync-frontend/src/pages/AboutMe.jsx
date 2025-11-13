import React from "react";
import "../styles/custom.css";

function AboutMe() {
  return (
    <div className="container m-0 p-0 about-wrapper">
      <div className="card shadow-lg border-0 p-5 mb-4 about-card bg-dark text-light">
        <div className="row align-items-center">
          {/* Profile Image */}
          <div className="col-md-4 text-center mb-4 mb-md-0">
            <img
              src="/RideSyncLOgo.jpg"
              alt="Rudra Naskar"
              className="img-fluid rounded-circle shadow about-img"
              style={{ width: "180px", height: "180px", objectFit: "cover" }}
            />
          </div>

          {/* About Content */}
          <div className="col-md-8">
            <h3 className="fw-bold mb-3 text-info">Hi, I'm Rudra Naskar </h3>
            <p className="text-light">
              I’m a passionate <strong>Backend-Focused Full-Stack Developer</strong> and a{" "}
              <strong>BCA student</strong> from Kolkata, India. I love designing and developing
              scalable web applications using modern technologies and clean architecture.
            </p>

            <p className="text-light">
              I specialize in <strong>Java</strong> and <strong>Spring Boot</strong> for backend development
              and have experience building <strong>RESTful APIs & Graphql APIs</strong> and integrating
              <strong> MySQL</strong> and <strong>MongoDB</strong> databases. On the frontend,
              I enjoy working with <strong>React.js</strong> and <strong>Bootstrap</strong> to deliver
              responsive, user-friendly interfaces.
            </p>

            <p className="text-light">
              My current project, <strong>RideSync v2</strong>, is a modern web application that helps
              riders schedule and manage vehicle maintenance efficiently — integrating API-driven
              data flow, user dashboards, and service tracking features.
            </p>

            <h5 className="mt-4 text-uppercase text-warning">
              Technologies I Work With
            </h5>
            <ul className="list-inline mt-3">
              <li className="list-inline-item badge bg-secondary me-2 mb-2">Java</li>
              <li className="list-inline-item badge bg-secondary me-2 mb-2">Spring Boot</li>
              {/* <li className="list-inline-item badge bg-secondary me-2 mb-2">React.js</li> */}
              <li className="list-inline-item badge bg-secondary me-2 mb-2">MongoDB Atlas</li>
              <li className="list-inline-item badge bg-secondary me-2 mb-2">MySQL</li>
              {/* <li className="list-inline-item badge bg-secondary me-2 mb-2">Bootstrap 5</li> */}
              {/* <li className="list-inline-item badge bg-secondary me-2 mb-2">GraphQL</li> */}
              <li className="list-inline-item badge bg-secondary me-2 mb-2">Docker</li>

               <li className="list-inline-item badge bg-secondary me-2 mb-2">OOPs</li>
               <li className="list-inline-item badge bg-secondary me-2 mb-2">Rest APIs</li>
               <li className="list-inline-item badge bg-secondary me-2 mb-2">GraphQL</li>
               <li className="list-inline-item badge bg-secondary me-2 mb-2">Docker</li>
               <li className="list-inline-item badge bg-secondary me-2 mb-2">Git & GitHub</li>
               <li className="list-inline-item badge bg-secondary me-2 mb-2">Linux</li>
               <li className="list-inline-item badge bg-secondary me-2 mb-2">Postman</li>
            </ul>

            <div className="mt-4">
              <a
                href="https://github.com/Rudra356"
                target="_blank"
                rel="noopener noreferrer"
                className="btn btn-outline-light me-2"
              >
                <i className="bi bi-github"></i> GitHub
              </a>
              <a
                href="https://linkedin.com/in/rudra-naskar653"
                target="_blank"
                rel="noopener noreferrer"
                className="btn btn-outline-primary"
              >
                <i className="bi bi-linkedin"></i> LinkedIn
              </a>
            </div>
             <div className="mt-4 small text-secondary">
              <p className="mb-1">
                📧 <strong>Email:</strong> rudranaskar71@gmail.com
              </p>
              <p className="mb-1">
                📱 <strong>Phone:</strong> +91 97489 95046
              </p>
              <p className="mb-0">
                📍 <strong>Location:</strong> Kolkata, West Bengal
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default AboutMe;
