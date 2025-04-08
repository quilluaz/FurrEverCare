import { useState } from "react"
import { format } from "date-fns"
import NavBar from "../components/UserNavBar"

function WellnessTimeline() {
  const [selectedPet, setSelectedPet] = useState("buddy")
  const [selectOpen, setSelectOpen] = useState(false)

  const pets = [
    { id: "buddy", name: "Buddy", type: "Dog", image: "/placeholder.svg" },
    { id: "whiskers", name: "Whiskers", type: "Cat", image: "/placeholder.svg" },
  ]

  const selectedPetData = pets.find((pet) => pet.id === selectedPet)

  // Timeline data (simplified version)
  const timelineEvents = [
    { 
      id: 1, 
      title: "Vaccination", 
      type: "MEDICAL", 
      date: "Aug 10", 
      color: "#EA6C7B" 
    },
    { 
      id: 2, 
      title: "Diet Plan", 
      type: "NUTRITION", 
      date: "Aug 12-15", 
      color: "#8A973F" 
    },
    { 
      id: 3, 
      title: "Grooming", 
      type: "CARE", 
      date: "Aug 18", 
      color: "#F0B542" 
    }
  ]

  const handleSelectChange = (petId) => {
    setSelectedPet(petId)
    setSelectOpen(false)
  }

  const styles = {
    container: {
      minHeight: '100%',
      width: '100%',
      overflowX: 'hidden',
      backgroundColor: '#FFF7EC',
      fontFamily: 'Arial, sans-serif'
    },
    mainContent: {
      maxWidth: '1280px',
      margin: '0 auto',
      padding: '32px 16px'
    },
    gridContainer: {
      display: 'grid',
      gridTemplateColumns: '1fr',
      gap: '24px'
    },
    mainColumn: {
      display: 'flex',
      flexDirection: 'column',
      gap: '24px'
    },
    welcomeSection: {
      backgroundColor: 'white',
      borderRadius: '16px',
      boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
      padding: '24px'
    },
    headerFlex: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      marginBottom: '24px',
      flexWrap: 'wrap'
    },
    profileFlex: {
      display: 'flex',
      alignItems: 'center',
      gap: '16px'
    },
    avatar: {
      height: '48px',
      width: '48px',
      borderRadius: '50%',
      backgroundColor: '#E5E7EB',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      fontSize: '20px',
      fontWeight: 'bold',
      color: '#042C3C',
      overflow: 'hidden'
    },
    avatarImg: {
      width: '100%',
      height: '100%',
      objectFit: 'cover'
    },
    heading: {
      fontSize: '24px',
      fontWeight: 'bold',
      color: '#042C3C',
      margin: 0
    },
    date: {
      color: '#6B7280',
      margin: 0
    },
    selectContainer: {
      width: '200px',
      position: 'relative'
    },
    selectTrigger: {
      backgroundColor: '#FFF7EC',
      border: '2px solid #EA6C7B',
      borderRadius: '9999px',
      padding: '8px 16px',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      cursor: 'pointer',
      color: '#042C3C'
    },
    selectDropdown: {
      position: 'absolute',
      top: '100%',
      left: 0,
      width: '100%',
      backgroundColor: 'white',
      borderRadius: '8px',
      boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
      marginTop: '4px',
      zIndex: 10,
      overflow: 'hidden',
      display: selectOpen ? 'block' : 'none'
    },
    selectOption: {
      padding: '8px 16px',
      cursor: 'pointer',
      transition: 'background-color 0.2s',
      color: '#042C3C'
    },
    selectOptionHover: {
      backgroundColor: '#F3F4F6'
    },
    chevronDown: {
      width: '16px',
      height: '16px',
      transition: 'transform 0.2s',
      transform: selectOpen ? 'rotate(180deg)' : 'rotate(0)'
    },
    promoBanner: {
      position: 'relative',
      backgroundColor: '#FFF7EC',
      borderRadius: '12px',
      padding: '24px',
      marginBottom: '24px'
    },
    promoContent: {
      maxWidth: '60%'
    },
    promoHeading: {
      fontSize: '20px',
      fontWeight: '600',
      color: '#042C3C',
      marginBottom: '8px'
    },
    promoText: {
      color: '#6B7280',
      marginBottom: '16px'
    },
    promoButton: {
      backgroundColor: '#EA6C7B',
      color: 'white',
      border: 'none',
      padding: '8px 16px',
      borderRadius: '6px',
      cursor: 'pointer',
      fontWeight: '500'
    },
    promoImage: {
      position: 'absolute',
      right: '24px',
      top: '50%',
      transform: 'translateY(-50%)',
      width: '30%',
      height: 'auto'
    },
    timelineSection: {
      backgroundColor: 'white',
      borderRadius: '12px'
    },
    timelineHeader: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      padding: '16px',
      borderBottom: '1px solid #E5E7EB'
    },
    timelineTitle: {
      fontSize: '18px',
      fontWeight: '600',
      color: '#042C3C',
      margin: 0
    },
    buttonGroup: {
      display: 'flex',
      gap: '8px'
    },
    outlineButton: {
      backgroundColor: 'transparent',
      border: '1px solid #E5E7EB',
      borderRadius: '6px',
      padding: '4px 8px',
      fontSize: '14px',
      cursor: 'pointer'
    },
    activeButton: {
      backgroundColor: '#8A973F',
      color: 'white',
      border: '1px solid #8A973F'
    },
    timelineContainer: {
      overflowX: 'auto',
      padding: '16px'
    },
    timelineGrid: {
      display: 'grid',
      gridTemplateColumns: 'repeat(7, 1fr)',
      gap: '8px',
      marginBottom: '16px'
    },
    timelineDay: {
      textAlign: 'center'
    },
    timelineDayName: {
      fontSize: '14px',
      color: '#6B7280'
    },
    timelineDayNumber: {
      fontSize: '16px',
      fontWeight: '600',
      color: '#042C3C'
    },
    timelineEventsContainer: {
      position: 'relative',
      minHeight: '200px'
    },
    timelineEvent: {
      position: 'absolute',
      height: '48px',
      borderRadius: '8px',
      padding: '8px 12px',
      color: 'white',
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'center'
    },
    timelineEventTitle: {
      fontWeight: '500',
      fontSize: '14px'
    },
    timelineEventType: {
      fontSize: '12px',
      opacity: '0.9'
    },
    sidebar: {
      display: 'flex',
      flexDirection: 'column',
      gap: '24px'
    },
    card: {
      backgroundColor: 'white',
      borderRadius: '8px',
      padding: '24px',
      boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)'
    },
    cardTitle: {
      fontSize: '18px',
      fontWeight: '600',
      color: '#042C3C',
      marginBottom: '16px',
      marginTop: 0
    },
    progressContainer: {
      marginBottom: '24px'
    },
    progressHeader: {
      display: 'flex',
      justifyContent: 'space-between',
      marginBottom: '8px'
    },
    progressLabel: {
      fontSize: '14px',
      color: '#6B7280'
    },
    progressValue: {
      fontSize: '14px',
      fontWeight: '500',
      color: '#042C3C'
    },
    progressBar: {
      height: '8px',
      backgroundColor: '#FFF7EC',
      borderRadius: '4px',
      position: 'relative'
    },
    progressIndicator: {
      position: 'absolute',
      height: '100%',
      width: '61%',
      backgroundColor: '#EA6C7B',
      borderRadius: '4px'
    },
    statsGrid: {
      display: 'grid',
      gridTemplateColumns: '1fr 1fr',
      gap: '16px'
    },
    statCard: {
      backgroundColor: '#FFF7EC',
      borderRadius: '12px',
      padding: '16px'
    },
    statValue: {
      fontSize: '24px',
      fontWeight: 'bold',
      color: '#042C3C',
      margin: 0
    },
    statLabel: {
      fontSize: '14px',
      color: '#6B7280',
      margin: 0
    },
    cardHeader: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      marginBottom: '16px'
    },
    ghostButton: {
      backgroundColor: 'transparent',
      border: 'none',
      color: '#EA6C7B',
      cursor: 'pointer',
      fontSize: '14px'
    },
    taskList: {
      display: 'flex',
      flexDirection: 'column',
      gap: '16px'
    },
    taskItem: {
      display: 'flex',
      alignItems: 'center',
      gap: '16px',
      padding: '12px',
      backgroundColor: '#FFF7EC',
      borderRadius: '12px'
    },
    taskContent: {
      flex: 1
    },
    taskTitle: {
      fontWeight: '500',
      color: '#042C3C',
      margin: 0
    },
    taskMeta: {
      display: 'flex',
      alignItems: 'center',
      gap: '8px',
      marginTop: '4px'
    },
    taskType: {
      fontSize: '12px',
      padding: '2px 8px',
      backgroundColor: '#8A973F',
      color: 'white',
      borderRadius: '9999px'
    },
    taskDate: {
      fontSize: '14px',
      color: '#6B7280'
    },
    smallAvatar: {
      height: '32px',
      width: '32px',
      borderRadius: '50%',
      backgroundColor: '#E5E7EB',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      fontSize: '14px',
      fontWeight: 'bold',
      color: '#042C3C',
      overflow: 'hidden'
    }
  }//create css for this

  // Media query styles for responsive design
  if (typeof window !== 'undefined' && window.innerWidth >= 1024) {
    styles.gridContainer = {
      ...styles.gridContainer,
      gridTemplateColumns: '3fr 1fr'
    }
  }

  // Generate dates for the timeline
  const today = new Date()
  const dates = Array.from({ length: 7 }).map((_, i) => {
    const date = new Date(today)
    date.setDate(today.getDate() - 3 + i)
    return date
  })

  return (
    <div style={styles.container}>
    <NavBar />
      {/* Header would go here */}
      <div style={styles.mainContent}>
        <div style={styles.gridContainer}>
          {/* Main Content */}
          <div style={styles.mainColumn}>
            {/* Welcome Section */}
            <div style={styles.welcomeSection}>
              <div style={styles.headerFlex}>
                <div style={styles.profileFlex}>
                  <div style={styles.avatar}>
                    {selectedPetData?.image ? (
                      <img 
                        src={selectedPetData.image || "/placeholder.svg"} 
                        alt={selectedPetData.name}
                        style={styles.avatarImg}
                      />
                    ) : (
                      selectedPetData?.name[0]
                    )}
                  </div>
                  <div>
                    <h1 style={styles.heading}>Hi, checking on {selectedPetData?.name}!</h1>
                    <p style={styles.date}>{format(new Date(), "MMMM d, yyyy")}</p>
                  </div>
                </div>
                <div style={styles.selectContainer}>
                  <div 
                    style={styles.selectTrigger}
                    onClick={() => setSelectOpen(!selectOpen)}
                  >
                    <span>{selectedPetData ? `${selectedPetData.name} (${selectedPetData.type})` : "Select a pet"}</span>
                    <svg 
                      style={styles.chevronDown} 
                      viewBox="0 0 24 24" 
                      fill="none" 
                      stroke="currentColor" 
                      strokeWidth="2" 
                      strokeLinecap="round" 
                      strokeLinejoin="round"
                    >
                      <polyline points="6 9 12 15 18 9"></polyline>
                    </svg>
                  </div>
                  <div style={styles.selectDropdown}>
                    {pets.map((pet) => (
                      <div 
                        key={pet.id} 
                        style={{
                          ...styles.selectOption,
                          backgroundColor: pet.id === selectedPet ? '#F3F4F6' : 'transparent'
                        }}
                        onClick={() => handleSelectChange(pet.id)}
                        onMouseOver={(e) => e.currentTarget.style.backgroundColor = '#F3F4F6'}
                        onMouseOut={(e) => e.currentTarget.style.backgroundColor = pet.id === selectedPet ? '#F3F4F6' : 'transparent'}
                      >
                        {pet.name} ({pet.type})
                      </div>
                    ))}
                  </div>
                </div>
              </div>



              {/* Timeline Section */}
              <div style={styles.timelineSection}>
                <div style={styles.timelineHeader}>
                  <h2 style={styles.timelineTitle}>Treatment Timeline</h2>
                  <div style={styles.buttonGroup}>
                    <button style={styles.outlineButton}>
                      List View
                    </button>
                    <button style={{...styles.outlineButton, ...styles.activeButton}}>
                      Timeline
                    </button>
                  </div>
                </div>
                <div style={styles.timelineContainer}>
                  {/* Custom Timeline Implementation */}
                  <div style={styles.timelineGrid}>
                    {dates.map((date, i) => (
                      <div key={i} style={styles.timelineDay}>
                        <div style={styles.timelineDayName}>{format(date, "EEE")}</div>
                        <div style={styles.timelineDayNumber}>{format(date, "d")}</div>
                      </div>
                    ))}
                  </div>
                  <div style={styles.timelineEventsContainer}>
                    {timelineEvents.map((event, index) => (
                      <div 
                        key={event.id}
                        style={{
                          ...styles.timelineEvent,
                          backgroundColor: event.color,
                          left: `${(index * 14) + 5}%`,
                          width: '25%',
                          top: `${index * 60}px`
                        }}
                      >
                        <div style={styles.timelineEventTitle}>{event.title}</div>
                        <div style={styles.timelineEventType}>{event.type}</div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Sidebar */}
          <div style={styles.sidebar}>
            {/* Overall Progress */}
            <div style={styles.card}>
              <h2 style={styles.cardTitle}>Overall Progress</h2>
              <div style={styles.progressContainer}>
                <div style={styles.progressHeader}>
                  <span style={styles.progressLabel}>Daily plan</span>
                  <span style={styles.progressValue}>61%</span>
                </div>
                <div style={styles.progressBar}>
                  <div style={styles.progressIndicator}></div>
                </div>
              </div>
              <div style={styles.statsGrid}>
                <div style={styles.statCard}>
                  <div style={styles.statValue}>13</div>
                  <div style={styles.statLabel}>Tasks Planned</div>
                </div>
                <div style={styles.statCard}>
                  <div style={styles.statValue}>10</div>
                  <div style={styles.statLabel}>Tasks Done</div>
                </div>
              </div>
            </div>

            {/* Upcoming Tasks */}
            <div style={styles.card}>
              <div style={styles.cardHeader}>
                <h2 style={styles.cardTitle}>Upcoming Tasks</h2>
                <button style={styles.ghostButton}>
                  View All
                </button>
              </div>
              <div style={styles.taskList}>
                {[
                  {
                    title: "Annual Checkup",
                    type: "VET VISIT",
                    date: "Aug 15",
                    avatar: "/placeholder.svg",
                  },
                  {
                    title: "Dental Cleaning",
                    type: "TREATMENT",
                    date: "Aug 18",
                    avatar: "/placeholder.svg",
                  },
                  {
                    title: "Vaccination Due",
                    type: "VACCINATION",
                    date: "Aug 20",
                    avatar: "/placeholder.svg",
                  },
                ].map((task, i) => (
                  <div key={i} style={styles.taskItem}>
                    <div style={styles.taskContent}>
                      <h3 style={styles.taskTitle}>{task.title}</h3>
                      <div style={styles.taskMeta}>
                        <span style={styles.taskType}>{task.type}</span>
                        <span style={styles.taskDate}>{task.date}</span>
                      </div>
                    </div>
                    <div style={styles.smallAvatar}>
                      {task.avatar ? (
                        <img 
                          src={task.avatar || "/placeholder.svg"} 
                          alt="Task" 
                          style={styles.avatarImg}
                        />
                      ) : (
                        "VT"
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default WellnessTimeline;
