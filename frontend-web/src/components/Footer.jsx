import { Box, Container, Typography, Grid, Link, IconButton } from "@mui/material"
import { Facebook, Twitter, Instagram, LinkedIn, Email, Phone, LocationOn } from "@mui/icons-material"

export default function Footer() {
  const colors = {
    yellow: "#F0B542",
    darkBlue: "#042C3C",
    coral: "#EA6C7B",
    cream: "#FFF7EC",
    green: "#8A9A5B",
  }

  const footerSections = [
    {
      title: "Services",
      links: [
        { name: "Pet Health Tracking", url: "#" },
        { name: "Emergency Profiles", url: "#" },
        { name: "First Aid Instructions", url: "#" },
        { name: "Health Timeline", url: "#" },
        { name: "Symptom Checker", url: "#" },
      ],
    },
    {
      title: "Resources",
      links: [
        { name: "Pet Care Blog", url: "#" },
        { name: "Health Guides", url: "#" },
        { name: "Medication Reminders", url: "#" },
        { name: "Vet Directory", url: "#" },
        { name: "FAQs", url: "#" },
      ],
    },
    {
      title: "Company",
      links: [
        { name: "About Us", url: "/about" },
        { name: "Our Team", url: "#" },
        { name: "Careers", url: "#" },
        { name: "Press", url: "#" },
        { name: "Contact", url: "#" },
      ],
    },
  ]

  return (
    <Box
      component="footer"
      sx={{
        backgroundColor: colors.darkBlue,
        color: "white",
        py: 6,
        position: "relative",
        overflow: "hidden",
      }}
    >
      {/* Decorative circle */}
      <Box
        sx={{
          position: "absolute",
          bottom: "-150px",
          right: "-150px",
          width: "300px",
          height: "300px",
          backgroundColor: `${colors.yellow}20`,
          borderRadius: "50%",
          zIndex: 0,
        }}
      />
      
      <Container maxWidth="lg" sx={{ position: "relative", zIndex: 1 }}>
        <Grid container spacing={4}>
          {/* Logo and description */}
          <Grid item xs={12} md={4}>
            <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
              <Box
                sx={{
                  width: 50,
                  height: 50,
                  borderRadius: "50%",
                  backgroundColor: colors.coral,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  mr: 2,
                }}
              >
                <Typography
                  variant="h6"
                  sx={{
                    fontFamily: "'Baloo 2', cursive",
                    fontWeight: "bold",
                    color: "white",
                  }}
                >
                  FC
                </Typography>
              </Box>
              <Typography
                variant="h5"
                sx={{
                  fontFamily: "'Baloo 2', cursive",
                  fontWeight: "bold",
                }}
              >
                FurrEverCare
              </Typography>
            </Box>
            <Typography variant="body2" sx={{ mb: 3, maxWidth: "90%" }}>
              Empowering pet owners with tools to manage their pets' health and access critical information when it matters most.
            </Typography>
            <Box sx={{ display: "flex", gap: 1 }}>
              <IconButton size="small" sx={{ color: colors.yellow }}>
                <Facebook />
              </IconButton>
              <IconButton size="small" sx={{ color: colors.yellow }}>
                <Twitter />
              </IconButton>
              <IconButton size="small" sx={{ color: colors.yellow }}>
                <Instagram />
              </IconButton>
              <IconButton size="small" sx={{ color: colors.yellow }}>
                <LinkedIn />
              </IconButton>
            </Box>
          </Grid>

          {/* Footer sections */}
          {footerSections.map((section, index) => (
            <Grid item xs={12} sm={6} md={2} key={index}>
              <Typography
                variant="subtitle1"
                sx={{
                  fontFamily: "'Baloo 2', cursive",
                  fontWeight: "bold",
                  mb: 2,
                  color: colors.yellow,
                }}
              >
                {section.title}
              </Typography>
              <Box component="ul" sx={{ listStyle: "none", p: 0, m: 0 }}>
                {section.links.map((link, i) => (
                  <Box component="li" key={i} sx={{ mb: 1 }}>
                    <Link
                      href={link.url}
                      sx={{
                        color: "white",
                        textDecoration: "none",
                        fontSize: "0.9rem",
                        transition: "color 0.2s",
                        "&:hover": {
                          color: colors.coral,
                        },
                      }}
                    >
                      {link.name}
                    </Link>
                  </Box>
                ))}
              </Box>
            </Grid>
          ))}

          {/* Contact information */}
          <Grid item xs={12} sm={6} md={4}>
            <Typography
              variant="subtitle1"
              sx={{
                fontFamily: "'Baloo 2', cursive",
                fontWeight: "bold",
                mb: 2,
                color: colors.yellow,
              }}
            >
              Contact Us
            </Typography>
            <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
              <Box sx={{ display: "flex", alignItems: "center" }}>
                <Email sx={{ mr: 1, color: colors.coral, fontSize: "1.2rem" }} />
                <Typography variant="body2">support@furrevercare.com</Typography>
              </Box>
              <Box sx={{ display: "flex", alignItems: "center" }}>
                <Phone sx={{ mr: 1, color: colors.coral, fontSize: "1.2rem" }} />
                <Typography variant="body2">+1 (555) 123-4567</Typography>
              </Box>
              <Box sx={{ display: "flex", alignItems: "flex-start" }}>
                <LocationOn sx={{ mr: 1, color: colors.coral, fontSize: "1.2rem", mt: 0.3 }} />
                <Typography variant="body2">
                  123 Pet Care Avenue<br />
                  San Francisco, CA 94158
                </Typography>
              </Box>
            </Box>
          </Grid>
        </Grid>

        <Box
          sx={{
            borderTop: `1px solid ${colors.cream}30`,
            mt: 5,
            pt: 3,
            display: "flex",
            flexDirection: { xs: "column", sm: "row" },
            justifyContent: "space-between",
            alignItems: { xs: "center", sm: "flex-start" },
            gap: 2,
          }}
        >
          <Typography variant="body2" sx={{ color: `${colors.cream}80` }}>
            © {new Date().getFullYear()} FurrEverCare. All rights reserved.
          </Typography>
          <Box sx={{ display: "flex", gap: 3 }}>
            <Link href="#" sx={{ color: `${colors.cream}80`, fontSize: "0.8rem", textDecoration: "none", "&:hover": { color: colors.coral } }}>
              Privacy Policy
            </Link>
            <Link href="#" sx={{ color: `${colors.cream}80`, fontSize: "0.8rem", textDecoration: "none", "&:hover": { color: colors.coral } }}>
              Terms of Service
            </Link>
            <Link href="#" sx={{ color: `${colors.cream}80`, fontSize: "0.8rem", textDecoration: "none", "&:hover": { color: colors.coral } }}>
              Cookie Policy
            </Link>
          </Box>
        </Box>
      </Container>
    </Box>
  )
}
