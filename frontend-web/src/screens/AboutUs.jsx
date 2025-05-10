
import { useState, useEffect, useRef } from "react"
import { Box, Typography, Button, Container, Card, CardContent } from "@mui/material"
import GuestNavBar from "../components/GuestNavBar"
import AboutUsDog2 from "../assets/AboutUsDog2.png"
import SignUpModal from "../components/SignUpModal"
import { useNavigate } from "react-router-dom"
import Footer from "../components/Footer"
import MobileDev from "../assets/MobileDev.jpg"
import BackEndDev from "../assets/BackEndDev.png"
import WebDev from "../assets/WebDev.jpg"

export default function AboutUs() {
  const colors = {
    yellow: "#F0B542",
    darkBlue: "#042C3C",
    coral: "#EA6C7B",
    cream: "#FFF7EC",
    green: "#8A9A5B",
  }

  const [isSignUpOpen, setIsSignUpOpen] = useState(false)
  const [isTeamVisible, setIsTeamVisible] = useState(false)
  const teamSectionRef = useRef(null)
  const navigate = useNavigate()

  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setIsTeamVisible(true)
        }
      },
      { threshold: 0.1 },
    )

    if (teamSectionRef.current) {
      observer.observe(teamSectionRef.current)
    }

    return () => {
      if (teamSectionRef.current) {
        observer.unobserve(teamSectionRef.current)
      }
    }
  }, [])

  const teamMembers = [
    {
      name: "Jan Isaac S. Quilo",
      role: "Mobile Developer",
      description: "Bringing the FurrEverCare experience to your pocket with our intuitive mobile applications.",
      image: MobileDev
    },
    {
      name: "Jeric Kiel B. Melocoton",
      role: "Backend Developer",
      description: "Architecting robust systems that power our platform with security and performance in mind.",
      image: BackEndDev
    },
    {
      name: "Selina Mae V. Genosolango",
      role: "Web Developer",
      description: "Passionate about creating beautiful, responsive user interfaces that delight our users.",
      image: WebDev
    },
  ]

  return (
    <Box
      sx={{
        minHeight: "100vh",
        width: "100%",
        backgroundColor: colors.cream,
        position: "relative",
        overflowX: "hidden",
        fontFamily: "'Plus Jakarta Sans', sans-serif",
        display: "flex",
        flexDirection: "column",
      }}
    >
      <GuestNavBar colors={colors} />
      <Typography
        sx={{
          pt: 2,
          pl: 2,
          color: "text.secondary",
          fontSize: "16px",
        }}
      >
        About Us Page
      </Typography>

      {/* Hero Section */}
      <Box
        sx={{
          height: "100vh",
          width: "100%",
          position: "relative",
        }}
      >
        <Box
          sx={{
            position: "absolute",
            top: "-100px",
            left: "-20px",
            width: "550px",
            height: "550px",
            backgroundColor: colors.yellow,
            borderRadius: "50%",
            transform: "scale(1.2)",
            zIndex: 0,
          }}
        />
        <Container
          maxWidth="lg"
          sx={{
            display: "flex",
            flexGrow: 1,
            position: "relative",
            zIndex: 5,
            padding: "0 20px",
            height: "100%",
          }}
        >
          <Box
            component="img"
            src={AboutUsDog2}
            alt="Bernese Mountain Dog"
            sx={{
              height: "auto",
              width: "52%",
              objectFit: "contain",
              objectPosition: "left center",
              marginTop: "-250px",
              marginLeft: "-200px",
              filter: "drop-shadow(0 10px 15px rgba(0,0,0,0.15))",
              transition: "all 0.5s ease-in-out",
              "&:hover": {
                filter: "drop-shadow(0 15px 20px rgba(0,0,0,0.2))",
              },
            }}
          />
         {/**  <Box
            sx={{
              position: "relative",
              height: "auto",
              width: "58%",
              marginTop: "-90px",
              marginLeft: "-600px",
              "&::before": {
                content: '""',
                position: "absolute",
                top: 0,
                left: 0,
                right: 0,
                bottom: 0,
                borderRadius: "40% 60% 70% 30% / 40% 50% 60% 50%",
                border: `3px solid ${colors.green}`,
                opacity: 0.7,
                transform: "scale(0.95)",
                transition: "all 0.5s ease-in-out",
              },
              "&:hover::before": {
                transform: "scale(1.02)",
                borderRadius: "60% 40% 30% 70% / 60% 30% 70% 40%",
                opacity: 0.9,
              },
              animation: "float 6s ease-in-out infinite",
              "@keyframes float": {
                "0%": {
                  transform: "translateY(0px)",
                },
                "50%": {
                  transform: "translateY(-15px)",
                },
                "100%": {
                  transform: "translateY(0px)",
                },
              },
            }}
          >
          </Box>**/}
          <Box
            sx={{
              width: "65%",
              display: "flex",
              flexDirection: "column",
              justifyContent: "center",
              alignItems: "center",
              padding: "0 20px",
              marginLeft: "100px",
            }}
          >
            <Typography
              variant="h1"
              sx={{
                fontSize: "100px",
                fontWeight: "bold",
                marginTop: "-180px",
                marginBottom: "20px",
                textAlign: "center",
                fontFamily: "'Baloo 2', cursive",
              }}
            >
              <Box component="span" sx={{ color: colors.green }}>
                STAY
              </Box>
              <Box component="span" sx={{ color: colors.darkBlue }}>
                {" "}
                on track.
              </Box>
            </Typography>
            <Typography
              variant="body1"
              sx={{
                fontSize: "16px",
                textAlign: "center",
                marginBottom: "30px",
                lineHeight: 1.4,
                color: colors.darkBlue,
              }}
            >
              Your pet's health, our priority! Track medications, monitor wellness, and explore expert care tips—all in
              one place. Join us and give your furry friend the love and care they deserve!
            </Typography>
            <Button
              variant="contained"
              sx={{
                backgroundColor: colors.coral,
                color: "white",
                borderRadius: "9999px",
                padding: "8px 32px",
                fontSize: "16px",
                textTransform: "none",
                boxShadow: "none",
                "&:hover": {
                  backgroundColor: colors.coral,
                  opacity: 0.9,
                  boxShadow: "none",
                },
              }}
              onClick={() => navigate("/signup")}
            >
              Sign Up
            </Button>
          </Box>
        </Container>
      </Box>
      <Box
  sx={{
    position: "absolute",
    top: 0,
    left: 0,
    width: "100%",
    marginTop: "600px",
    height: "100px",
    zIndex: 10, // Make sure this is higher than other elements
    pointerEvents: "none", // So it doesn't interfere with clicks
    overflow: "hidden",
  }}
>
  <svg
    style={{
      animation: "wave-motion 3s linear infinite",
      width: "100%",
      height: "100%",
    }}
    viewBox="0 0 1440 320"
    preserveAspectRatio="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <path
      fill={colors.green}
      d="M0,160L60,170.7C120,181,240,203,360,197.3C480,192,600,160,720,154.7C840,149,960,171,1080,165.3C1200,160,1320,128,1380,112L1440,96L1440,320L1380,320C1320,320,1200,320,1080,320C960,320,840,320,720,320C600,320,480,320,360,320C240,320,120,320,60,320L0,320Z"
    />
  </svg>
</Box>

      {/* Meet the Team Section */}
      <Box
        ref={teamSectionRef}
        sx={{
          minHeight: "100vh",
          width: "100%",
          backgroundColor: colors.cream,
          position: "relative",
          padding: "80px 0",
          marginTop: "-120px"
        }}
      >
        <Container maxWidth="lg">
          <Box
            className={isTeamVisible ? "animate-fade-in" : ""}
            sx={{
              opacity: 0,
              transform: "translateY(50px)",
              transition: "opacity 0.8s ease-out, transform 0.8s ease-out",
              ...(isTeamVisible && {
                opacity: 1,
                transform: "translateY(0)",
              }),
            }}
          >
            <Typography
              variant="h2"
              sx={{
                fontSize: { xs: "40px", md: "60px" },
                fontWeight: "bold",
                marginBottom: "60px",
                textAlign: "center",
                fontFamily: "'Baloo 2', cursive",
                color: colors.darkBlue,
                position: "relative",
                "&:after": {
                  content: '""',
                  position: "absolute",
                  bottom: "-15px",
                  left: "50%",
                  transform: "translateX(-50%)",
                  width: "80px",
                  height: "4px",
                  backgroundColor: colors.yellow,
                  borderRadius: "2px",
                },
              }}
            >
              Meet the Team
            </Typography>
          </Box>

          <Box
            sx={{
              display: "flex",
              flexDirection: { xs: "column", md: "row" },
              gap: 4,
              justifyContent: "center",
              alignItems: "stretch",
              width: "100%",
              overflowX: { xs: "hidden", md: "visible" },
            }}
          >
            {teamMembers.map((member, index) => (
              <Box
                key={index}
                className={isTeamVisible ? `animate-fade-in-delay-${index}` : ""}
                sx={{
                  opacity: 0,
                  transform: "translateY(50px)",
                  transition: "opacity 0.8s ease-out, transform 0.8s ease-out",
                  transitionDelay: `${0.2 * (index + 1)}s`,
                  ...(isTeamVisible && {
                    opacity: 1,
                    transform: "translateY(0)",
                  }),
                  flex: { xs: "1 1 100%", md: "1 1 0" },
                  minWidth: { md: "30%" },
                }}
              >
                <Card
                  sx={{
                    height: "100%",
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                    padding: "30px 20px",
                    borderRadius: "16px",
                    boxShadow: "0 4px 20px rgba(0,0,0,0.08)",
                    transition: "transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out",
                    "&:hover": {
                      transform: "translateY(-10px)",
                      boxShadow: "0 10px 30px rgba(0,0,0,0.12)",
                    },
                    backgroundColor:
                      index === 0 ? `${colors.yellow}20` : index === 1 ? `${colors.darkBlue}15` : `${colors.coral}15`,
                    border: `2px solid ${index === 0 ? colors.yellow : index === 1 ? colors.darkBlue : colors.coral}`,
                  }}
                >
                  <Box
                    sx={{
                      width: 150,
                      height: 150,
                      borderRadius: "50%",
                      overflow: "hidden",
                      marginBottom: 2,
                      border: `4px solid ${index === 0 ? colors.yellow : index === 1 ? colors.darkBlue : colors.coral}`,
                    }}
                  >
                    <Box
                      component="img"
                      src={member.image}
                      alt={member.name}
                      sx={{
                        width: "100%",
                        height: "100%",
                        objectFit: "cover",
                      }}
                    />
                  </Box>
                  <CardContent sx={{ textAlign: "center" }}>
                    <Typography
                      variant="h5"
                      sx={{
                        fontFamily: "'Baloo 2', cursive",
                        fontWeight: "bold",
                        color: colors.darkBlue,
                        marginBottom: 1,
                      }}
                    >
                      {member.name}
                    </Typography>
                    <Typography
                      variant="subtitle1"
                      sx={{
                        fontFamily: "'Baloo 2', cursive",
                        color: index === 0 ? colors.yellow : index === 1 ? colors.darkBlue : colors.coral,
                        fontWeight: "bold",
                        marginBottom: 2,
                      }}
                    >
                      {member.role}
                    </Typography>
                    <Typography
                      variant="body2"
                      sx={{
                        color: colors.darkBlue,
                        lineHeight: 1.6,
                      }}
                    >
                      {member.description}
                    </Typography>
                  </CardContent>
                </Card>
              </Box>
            ))}
          </Box>
        </Container>
      </Box>
      <Footer />

      <style
        dangerouslySetInnerHTML={{
          __html: `
            @import url('https://fonts.googleapis.com/css2?family=Baloo+2:wght@400;500;600;700;800&family=Plus+Jakarta+Sans:wght@200;300;400;500;600;700;800&display=swap');
            html, body {
              margin: 0;
              padding: 0;
              height: 100%;
              width: 100%;
              overflow-x: hidden;
              scroll-behavior: smooth;
            }
            #root {
              min-height: 100vh;
              display: flex;
              flex-direction: column;
            }
          `,
        }}
      />

      {isSignUpOpen && (
        <SignUpModal
          onClose={() => setIsSignUpOpen(false)}
          onSignUp={(firstName, lastName, phoneNumber, email, password, confirmPassword) => {
            console.log("Sign up with:", firstName, lastName, phoneNumber, email, password)
            setIsSignUpOpen(false)
          }}
        />
      )}
    </Box>
  )
}
