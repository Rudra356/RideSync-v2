
import React from "react";
import "../styles/custom.css";

const skillGroups = [
  {
    title: "Programming",
    skills: ["Java", "Python", "OOP", "Java 8"],
  },
  {
    title: "Backend Engineering",
    skills: [
      "Spring Boot",
      "Spring MVC",
      "Spring Data JPA",
      "Spring Security",
      "REST APIs",
      "Microservices",
      "OAuth 2",
      "OpenFeign",
      "RestTemplate",
      "Layered Architecture",
      "Exception Handling",
      "CRUD Operations",
    ],
  },
  {
    title: "Testing & Development",
    skills: ["Postman", "API Testing", "JUnit", "Mockito", "SDLC"],
  },
  {
    title: "Database & Infrastructure",
    skills: [
      "MySQL",
      "Docker",
      "AWS IAM",
      "AWS EC2",
      "AWS S3",
      "Linux",
      "Git",
      "GitHub",
    ],
  },
  {
    title: "Frontend Fundamentals",
    skills: ["React.js", "HTML", "CSS"],
  },
];

function AboutMe() {
  return (
    <main className="rs-about-page">
      <div className="rs-about-container">

        {/* HERO SECTION */}
        <section className="rs-about-hero">
          <div className="rs-about-intro">
            <span className="rs-about-overline">
              THE DEVELOPER BEHIND RIDESYNC
            </span>

            <h1>
              Building systems
              <br />
              <span>that work.</span>
            </h1>

            <p className="rs-about-lead">
              I'm Rudra Naskar, a Backend Developer focused on building
              scalable applications, reliable APIs and structured backend
              systems using Java and Spring technologies.
            </p>

            <div className="rs-about-actions">
              <a
                href="https://github.com/Rudra356"
                target="_blank"
                rel="noopener noreferrer"
                className="rs-about-primary-link"
              >
                <i className="bi bi-github"></i>
                GitHub
                <span>↗</span>
              </a>

              <a
                href="https://linkedin.com/in/rudra-naskar653"
                target="_blank"
                rel="noopener noreferrer"
                className="rs-about-secondary-link"
              >
                <i className="bi bi-linkedin"></i>
                LinkedIn
                <span>↗</span>
              </a>
            </div>
          </div>

          <div className="rs-about-profile">
            <div className="rs-about-image-frame">
              <img
                src="/RideSyncLOgo.jpg"
                alt="Rudra Naskar"
                className="rs-about-image"
              />
            </div>

            <div className="rs-about-profile-caption">
              <span>RUDRA NASKAR</span>
              <span>BACKEND DEVELOPER</span>
            </div>
          </div>
        </section>

        {/* PROFESSIONAL SUMMARY */}
        <section className="rs-about-section rs-about-summary">
          <div className="rs-about-section-heading">
            <span className="rs-about-section-number">01 / PROFILE</span>
            <h2>Engineering the backend.</h2>
          </div>

          <div className="rs-about-summary-content">
            <p>
              My work revolves around designing backend workflows, developing
              RESTful services and building applications with maintainable
              architecture. I work primarily with Java, Spring Boot and
              Spring MVC, with experience in authentication, validation,
              database integration and API-driven application development.
            </p>

            <p>
              During my Backend Developer internship at Adamas Tech Consulting,
              I worked on Spring MVC modules, authentication, user profile
              management, backend workflows and API testing. I also apply
              these concepts in personal projects, including RideSync v2.
            </p>
          </div>
        </section>

        {/* EXPERIENCE */}
        <section className="rs-about-section">
          <div className="rs-about-section-heading">
            <span className="rs-about-section-number">02 / EXPERIENCE</span>
            <h2>Where I've worked.</h2>
          </div>

          <article className="rs-about-experience">
            <div className="rs-about-experience-date">
              JUN 2024 — AUG 2024
            </div>

            <div className="rs-about-experience-details">
              <span className="rs-about-experience-type">
                PROFESSIONAL EXPERIENCE
              </span>

              <h3>Backend Developer Intern</h3>

              <h4>Adamas Tech Consulting</h4>

              <ul>
                <li>
                  Developed Spring MVC modules for authentication, form
                  validation and user profile management.
                </li>
                <li>
                  Implemented backend logic for login and account update
                  workflows.
                </li>
                <li>
                  Worked with REST APIs and performed API testing and
                  validation using Postman.
                </li>
                <li>
                  Assisted in debugging backend workflows and followed
                  MVC architecture.
                </li>
              </ul>
            </div>
          </article>
        </section>

        {/* PROJECT */}
        <section className="rs-about-section">
          <div className="rs-about-section-heading">
            <span className="rs-about-section-number">03 / SELECTED PROJECT</span>
            <h2>Built with purpose.</h2>
          </div>

          <article className="rs-about-project">
            <div className="rs-about-project-top">
              <div>
                <span className="rs-about-project-label">
                  PERSONAL PROJECT
                </span>
                <h3>RideSync v2</h3>
                <p>Full Stack Vehicle Maintenance Platform</p>
              </div>

              <span className="rs-about-project-index">01</span>
            </div>

            <div className="rs-about-project-body">
              <p>
                A vehicle maintenance management application designed to
                organize service schedules, maintenance records and
                replacement history through an API-driven architecture.
              </p>

              <ul>
                <li>
                  Developed REST APIs using Java and Spring Boot for
                  maintenance tracking and service scheduling.
                </li>
                <li>
                  Implemented MySQL database operations and CRUD workflows
                  with data consistency in mind.
                </li>
                <li>
                  Followed a layered Controller, Service and Repository
                  architecture.
                </li>
                <li>
                  Tested API responses, status codes and backend workflows
                  using Postman.
                </li>
              </ul>

              <a
                href="https://github.com/Rudra356/RideSync-v2"
                target="_blank"
                rel="noopener noreferrer"
                className="rs-about-project-link"
              >
                Explore project on GitHub <span>↗</span>
              </a>
            </div>
          </article>
        </section>

        {/* SKILLS */}
        <section className="rs-about-section">
          <div className="rs-about-section-heading">
            <span className="rs-about-section-number">04 / TECHNOLOGY</span>
            <h2>My technical toolkit.</h2>
            <p>
              Technologies and tools I use to build, test and maintain
              applications.
            </p>
          </div>

          <div className="rs-about-skills-grid">
            {skillGroups.map((group, index) => (
              <div className="rs-about-skill-group" key={group.title}>
                <div className="rs-about-skill-heading">
                  <span>0{index + 1}</span>
                  <h3>{group.title}</h3>
                </div>

                <div className="rs-about-skill-list">
                  {group.skills.map((skill) => (
                    <span className="rs-about-skill" key={skill}>
                      {skill}
                    </span>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </section>

        {/* CONTACT */}
        <section className="rs-about-contact">
          <div>
            <span className="rs-about-overline">LET'S CONNECT</span>
            <h2>Have a project in mind?</h2>
            <p>
              I'm open to connecting with developers, teams and opportunities
              in backend engineering.
            </p>
          </div>

          <a
            href="mailto:rudranaskar71@gmail.com"
            className="rs-about-contact-link"
          >
            Get in touch <span>↗</span>
          </a>
        </section>

        {/* CONTACT DETAILS */}
        <footer className="rs-about-footer">
          <div>
            <span>EMAIL</span>
            <a href="mailto:rudranaskar71@gmail.com">
              rudranaskar71@gmail.com
            </a>
          </div>

          <div>
            <span>LOCATION</span>
            <p>Kolkata, West Bengal, India</p>
          </div>

          <div>
            <span>PHONE</span>
            <a href="tel:+919748995046">+91 97489 95046</a>
          </div>
        </footer>

      </div>
    </main>
  );
}

export default AboutMe;
