import { useState, useEffect } from "react"
import { format, addDays, subDays, startOfWeek, endOfWeek, isSameDay, parseISO } from "date-fns"
import NavBar from "../components/UserNavBar"
import axios from "axios"
import AuthService from "../config/AuthService"
import { Loader, ChevronLeft, ChevronRight, Calendar } from "lucide-react"

function WellnessTimeline() {
  const [selectedPet, setSelectedPet] = useState("")
  const [selectOpen, setSelectOpen] = useState(false)
  const [userPets, setUserPets] = useState([])
  const [timelineEvents, setTimelineEvents] = useState([])
  const [allEvents, setAllEvents] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [currentDate, setCurrentDate] = useState(new Date())
  const [visibleDates, setVisibleDates] = useState([])
  const [showCalendar, setShowCalendar] = useState(false)
  const [selectedDate, setSelectedDate] = useState(null)

  const user = AuthService.getUser()
  const userID = user?.userId || null
  const API_BASE_URL = "https://furrevercare-deploy-13.onrender.com/api"

  // Update visible dates when current date changes
  useEffect(() => {
    const start = startOfWeek(currentDate, { weekStartsOn: 1 }) // Start from Monday
    const dates = []
    for (let i = 0; i < 7; i++) { // Show 1 week only
      dates.push(addDays(start, i))
    }
    setVisibleDates(dates)
  }, [currentDate])

  // Fetch user's pets and all activities
  useEffect(() => {
    const fetchAllData = async () => {
      if (!userID) {
        setError("Please log in to view the wellness timeline.")
        setLoading(false)
        return
      }
      setLoading(true)
      setError(null)
      try {
        // Fetch pets
        const petsRes = await axios.get(`${API_BASE_URL}/users/${userID}/pets`)
        const fetchedPets = Array.isArray(petsRes.data) ? petsRes.data : []
        setUserPets(fetchedPets)
        // Fetch all activities for all pets
        let allEventsArr = []
        for (const pet of fetchedPets) {
          // Treatment Plans
          const plansRes = await axios.get(`${API_BASE_URL}/users/${userID}/pets/${pet.petID}/treatmentPlans`)
          const plans = Array.isArray(plansRes.data) ? plansRes.data : []
          allEventsArr = allEventsArr.concat(
            plans.map(plan => ({
              id: `plan-${pet.petID}-${plan.planID}`,
              petID: pet.petID,
              petName: pet.name,
              title: plan.name,
              type: "TREATMENT",
              startDate: new Date(plan.startDate),
              endDate: plan.endDate ? new Date(plan.endDate) : null,
              color: "#EA6C7B",
              progress: plan.progressPercentage || 0,
              status: plan.status,
              description: plan.description
            }))
          )
          // Tasks
          const tasksRes = await axios.get(`${API_BASE_URL}/users/${userID}/pets/${pet.petID}/scheduledTasks`)
          const tasks = Array.isArray(tasksRes.data) ? tasksRes.data : []
          allEventsArr = allEventsArr.concat(
            tasks.map(task => ({
              id: `task-${pet.petID}-${task.taskID}`,
              petID: pet.petID,
              petName: pet.name,
              title: task.description,
              type: task.taskType || "TASK",
              startDate: new Date(task.scheduledDateTime),
              endDate: new Date(task.scheduledDateTime),
              color: task.status === "COMPLETED" ? "#8A973F" : "#F0B542",
              status: task.status,
              description: task.notes
            }))
          )
        }
        // Sort by startDate
        allEventsArr.sort((a, b) => a.startDate - b.startDate)
        setAllEvents(allEventsArr)
        setTimelineEvents(allEventsArr) // Default: all pets
      } catch (err) {
        console.error("Failed to fetch timeline data:", err)
        setError("Failed to load timeline data. Please try again.")
        if (err.response?.status === 403) {
          AuthService.clearAuth()
          window.location.href = "/login"
        }
      } finally {
        setLoading(false)
      }
    }
    fetchAllData()
  }, [userID])

  // Filter events when selectedPet changes
  useEffect(() => {
    if (!selectedPet) {
      setTimelineEvents(allEvents)
    } else {
      setTimelineEvents(allEvents.filter(e => e.petID === selectedPet))
    }
  }, [selectedPet, allEvents])

  const handleSelectChange = (petId) => {
    setSelectedPet(petId)
    setSelectOpen(false)
  }

  const navigateTimeline = (direction) => {
    setCurrentDate(prev => direction === 'next' ? addDays(prev, 7) : subDays(prev, 7))
  }

  const selectedPetData = userPets.find((pet) => pet.petID === selectedPet)

  // Get events for a specific date
  const getEventsForDate = (date) => {
    return timelineEvents.filter(event => {
      const eventStart = new Date(event.startDate)
      const eventEnd = event.endDate ? new Date(event.endDate) : eventStart
      return (
        (isSameDay(eventStart, date) || isSameDay(eventEnd, date)) ||
        (eventStart <= date && eventEnd >= date)
      )
    })
  }

  const handleDateSelect = (date) => {
    setSelectedDate(date)
    setCurrentDate(date)
    setShowCalendar(false)
  }

  const generateCalendarDays = (date) => {
    const year = date.getFullYear()
    const month = date.getMonth()
    const firstDay = new Date(year, month, 1)
    const lastDay = new Date(year, month + 1, 0)
    const days = []

    // Add empty cells for days before the first day of the month
    for (let i = 0; i < firstDay.getDay(); i++) {
      days.push(null)
    }

    // Add days of the month
    for (let i = 1; i <= lastDay.getDate(); i++) {
      days.push(new Date(year, month, i))
    }

    return days
  }

  const styles = {
    container: {
      minHeight: '100vh',
      width: '100%',
      overflowX: 'hidden',
      backgroundColor: '#FFF7EC',
      fontFamily: 'Arial, sans-serif'
    },
    mainContent: {
      maxWidth: '1100px',
      margin: '0 auto',
      padding: '16px 4px',
      position: 'relative'
    },
    gridContainer: {
      display: 'grid',
      gridTemplateColumns: '1fr',
      gap: '12px'
    },
    mainColumn: {
      display: 'flex',
      flexDirection: 'column',
      gap: '12px'
    },
    welcomeSection: {
      backgroundColor: 'white',
      borderRadius: '12px',
      boxShadow: '0 2px 4px rgba(0, 0, 0, 0.08)',
      padding: '12px'
    },
    headerFlex: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      marginBottom: '12px',
      flexWrap: 'wrap'
    },
    profileFlex: {
      display: 'flex',
      alignItems: 'center',
      gap: '8px'
    },
    avatar: {
      height: '36px',
      width: '36px',
      borderRadius: '50%',
      backgroundColor: '#E5E7EB',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      fontSize: '16px',
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
      fontSize: '18px',
      fontWeight: 'bold',
      color: '#042C3C',
      margin: 0
    },
    date: {
      color: '#6B7280',
      margin: 0,
      fontSize: '13px'
    },
    selectContainer: {
      width: '140px',
      position: 'relative'
    },
    selectTrigger: {
      backgroundColor: '#FFF7EC',
      border: '2px solid #EA6C7B',
      borderRadius: '9999px',
      padding: '4px 10px',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      cursor: 'pointer',
      color: '#042C3C',
      fontSize: '14px'
    },
    selectDropdown: {
      position: 'absolute',
      top: '100%',
      left: 0,
      width: '100%',
      backgroundColor: 'white',
      borderRadius: '8px',
      boxShadow: '0 2px 4px rgba(0, 0, 0, 0.08)',
      marginTop: '2px',
      zIndex: 10,
      overflow: 'hidden',
      display: selectOpen ? 'block' : 'none'
    },
    selectOption: {
      padding: '6px 10px',
      cursor: 'pointer',
      transition: 'background-color 0.2s',
      color: '#042C3C',
      fontSize: '14px'
    },
    selectOptionHover: {
      backgroundColor: '#F3F4F6'
    },
    chevronDown: {
      width: '14px',
      height: '14px',
      transition: 'transform 0.2s',
      transform: selectOpen ? 'rotate(180deg)' : 'rotate(0)'
    },
    promoBanner: {
      position: 'relative',
      backgroundColor: '#FFF7EC',
      borderRadius: '10px',
      padding: '12px',
      marginBottom: '12px'
    },
    promoContent: {
      maxWidth: '60%'
    },
    promoHeading: {
      fontSize: '16px',
      fontWeight: '600',
      color: '#042C3C',
      marginBottom: '6px'
    },
    promoText: {
      color: '#6B7280',
      marginBottom: '10px'
    },
    promoButton: {
      backgroundColor: '#EA6C7B',
      color: 'white',
      border: 'none',
      padding: '6px 12px',
      borderRadius: '6px',
      cursor: 'pointer',
      fontWeight: '500',
      fontSize: '14px'
    },
    promoImage: {
      position: 'absolute',
      right: '12px',
      top: '50%',
      transform: 'translateY(-50%)',
      width: '22%',
      height: 'auto'
    },
    timelineSection: {
      backgroundColor: 'white',
      borderRadius: '10px'
    },
    timelineHeader: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      padding: '8px',
      borderBottom: '1px solid #E5E7EB'
    },
    timelineTitle: {
      fontSize: '15px',
      fontWeight: '600',
      color: '#042C3C',
      margin: 0
    },
    buttonGroup: {
      display: 'flex',
      gap: '4px'
    },
    outlineButton: {
      backgroundColor: 'transparent',
      border: '1px solid #E5E7EB',
      borderRadius: '6px',
      padding: '2px 6px',
      fontSize: '13px',
      cursor: 'pointer'
    },
    activeButton: {
      backgroundColor: '#8A973F',
      color: 'white',
      border: '1px solid #8A973F'
    },
    timelineContainer: {
      overflowX: 'auto',
      padding: '8px',
      position: 'relative'
    },
    timelineNavigation: {
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      marginBottom: '8px',
      padding: '0 8px'
    },
    navButton: {
      backgroundColor: 'transparent',
      border: 'none',
      cursor: 'pointer',
      padding: '4px',
      borderRadius: '50%',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      color: '#042C3C',
      transition: 'background-color 0.2s'
    },
    navButtonHover: {
      backgroundColor: 'rgba(0, 0, 0, 0.05)'
    },
    timelineGrid: {
      display: 'grid',
      gridTemplateColumns: 'repeat(7, minmax(100px, 1fr))',
      gap: '6px',
      marginBottom: '8px',
      minWidth: '700px'
    },
    timelineDay: {
      textAlign: 'center',
      padding: '8px 4px 8px 4px',
      border: '1px solid #E5E7EB',
      borderRadius: '10px',
      backgroundColor: 'white',
      minHeight: '90px',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
    },
    timelineDayHeader: {
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      gap: '2px'
    },
    timelineDayName: {
      fontSize: '11px',
      color: '#6B7280',
      fontWeight: '500'
    },
    timelineDayNumber: {
      fontSize: '13px',
      fontWeight: '600',
      color: '#042C3C'
    },
    timelineDayToday: {
      backgroundColor: '#FFF7EC',
      borderColor: '#EA6C7B'
    },
    timelineEventsContainer: {
      position: 'relative',
      minHeight: '80px',
      padding: '6px 0'
    },
    timelineEvent: {
      position: 'relative',
      minHeight: '28px',
      borderRadius: '8px',
      padding: '6px 8px',
      color: 'white',
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'center',
      marginBottom: '7px',
      boxShadow: '0 1px 4px rgba(0,0,0,0.04)',
      fontSize: '12px',
      fontWeight: 500,
      transition: 'transform 0.2s',
    },
    timelineEventHover: {
      transform: 'scale(1.02)',
      zIndex: 10
    },
    timelineEventTitle: {
      fontWeight: '500',
      fontSize: '12px'
    },
    timelineEventType: {
      fontSize: '10px',
      opacity: '0.9'
    },
    sidebar: {
      display: 'flex',
      flexDirection: 'column',
      gap: '12px',
      marginRight: '20px'
    },
    card: {
      backgroundColor: 'white',
      borderRadius: '8px',
      padding: '12px',
      boxShadow: '0 1px 2px rgba(0, 0, 0, 0.08)'
    },
    cardTitle: {
      fontSize: '15px',
      fontWeight: '600',
      color: '#042C3C',
      marginBottom: '8px',
      marginTop: 0
    },
    progressContainer: {
      marginBottom: '12px'
    },
    progressHeader: {
      display: 'flex',
      justifyContent: 'space-between',
      marginBottom: '4px'
    },
    progressLabel: {
      fontSize: '11px',
      color: '#6B7280'
    },
    progressValue: {
      fontSize: '11px',
      fontWeight: '500',
      color: '#042C3C'
    },
    progressBar: {
      height: '6px',
      backgroundColor: '#FFF7EC',
      borderRadius: '3px',
      position: 'relative'
    },
    progressIndicator: {
      position: 'absolute',
      height: '100%',
      width: '61%',
      backgroundColor: '#EA6C7B',
      borderRadius: '3px'
    },
    statsGrid: {
      display: 'grid',
      gridTemplateColumns: '1fr 1fr',
      gap: '8px'
    },
    statCard: {
      backgroundColor: '#FFF7EC',
      borderRadius: '8px',
      padding: '8px'
    },
    statValue: {
      fontSize: '16px',
      fontWeight: 'bold',
      color: '#042C3C',
      margin: 0
    },
    statLabel: {
      fontSize: '11px',
      color: '#6B7280',
      margin: 0
    },
    cardHeader: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      marginBottom: '8px'
    },
    ghostButton: {
      backgroundColor: 'transparent',
      border: 'none',
      color: '#EA6C7B',
      cursor: 'pointer',
      fontSize: '12px'
    },
    taskList: {
      display: 'flex',
      flexDirection: 'column',
      gap: '8px'
    },
    taskItem: {
      display: 'flex',
      alignItems: 'center',
      gap: '8px',
      padding: '6px',
      backgroundColor: '#FFF7EC',
      borderRadius: '8px'
    },
    taskContent: {
      flex: 1
    },
    taskTitle: {
      fontWeight: '500',
      color: '#042C3C',
      margin: 0,
      fontSize: '13px'
    },
    taskMeta: {
      display: 'flex',
      alignItems: 'center',
      gap: '4px',
      marginTop: '2px'
    },
    taskType: {
      fontSize: '10px',
      padding: '1px 6px',
      backgroundColor: '#8A973F',
      color: 'white',
      borderRadius: '9999px'
    },
    taskDate: {
      fontSize: '11px',
      color: '#6B7280'
    },
    smallAvatar: {
      height: '22px',
      width: '22px',
      borderRadius: '50%',
      backgroundColor: '#E5E7EB',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      fontSize: '11px',
      fontWeight: 'bold',
      color: '#042C3C',
      overflow: 'hidden'
    },
    calendarIcon: {
      cursor: 'pointer',
      padding: '4px',
      borderRadius: '50%',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      color: '#042C3C',
      transition: 'background-color 0.2s'
    },
    calendarIconHover: {
      backgroundColor: 'rgba(0, 0, 0, 0.05)'
    },
    modalOverlay: {
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(0, 0, 0, 0.5)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 1000
    },
    calendarModal: {
      backgroundColor: 'white',
      borderRadius: '10px',
      boxShadow: '0 2px 4px rgba(0, 0, 0, 0.08)',
      padding: '12px',
      width: '240px',
      position: 'relative'
    },
    calendarHeader: {
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      marginBottom: '10px'
    },
    calendarTitle: {
      fontSize: '14px',
      fontWeight: '600',
      color: '#042C3C'
    },
    closeButton: {
      position: 'absolute',
      top: '8px',
      right: '8px',
      background: 'none',
      border: 'none',
      cursor: 'pointer',
      padding: '2px',
      color: '#6B7280',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      borderRadius: '50%',
      transition: 'background-color 0.2s'
    },
    closeButtonHover: {
      backgroundColor: 'rgba(0, 0, 0, 0.05)'
    },
    calendarGrid: {
      display: 'grid',
      gridTemplateColumns: 'repeat(7, 1fr)',
      gap: '4px'
    },
    calendarWeekday: {
      textAlign: 'center',
      fontSize: '10px',
      color: '#6B7280',
      padding: '4px 2px',
      fontWeight: '500'
    },
    calendarDay: {
      padding: '4px',
      textAlign: 'center',
      cursor: 'pointer',
      borderRadius: '6px',
      fontSize: '11px',
      transition: 'all 0.2s',
      color: '#4B5563'
    },
    calendarDayHover: {
      backgroundColor: '#F3F4F6'
    },
    calendarDaySelected: {
      backgroundColor: '#EA6C7B',
      color: 'white'
    },
    calendarDayToday: {
      border: '2px solid #EA6C7B'
    },
    calendarDayEmpty: {
      padding: '4px'
    },
    calendarNavigation: {
      display: 'flex',
      alignItems: 'center',
      gap: '8px'
    },
    calendarNavButton: {
      background: 'none',
      border: 'none',
      cursor: 'pointer',
      padding: '2px',
      color: '#042C3C',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      borderRadius: '50%',
      transition: 'background-color 0.2s'
    },
    calendarNavButtonHover: {
      backgroundColor: 'rgba(0, 0, 0, 0.05)'
    }
  }

  // Media query styles for responsive design
  if (typeof window !== 'undefined' && window.innerWidth >= 1024) {
    styles.gridContainer = {
      ...styles.gridContainer,
      gridTemplateColumns: '3fr 1fr'
    }
  }

  return (
    <div style={styles.container}>
      <NavBar />
      <div style={styles.mainContent}>
        <div style={styles.gridContainer}>
          <div style={styles.mainColumn}>
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
                      selectedPetData?.name?.[0] || "?"
                    )}
                  </div>
                  <div>
                    <h1 style={styles.heading}>
                      {selectedPetData ? `Hi, checking on ${selectedPetData.name}!` : "Select a pet to view timeline"}
                    </h1>
                    <p style={styles.date}>{format(new Date(), "MMMM d, yyyy")}</p>
                  </div>
                </div>
                <div style={styles.selectContainer}>
                  <div 
                    style={styles.selectTrigger}
                    onClick={() => setSelectOpen(!selectOpen)}
                  >
                      <span>{selectedPetData ? selectedPetData.name : "Select a pet"}</span>
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
                    {userPets.map((pet) => (
                      <div 
                        key={pet.petID} 
                        style={{
                          ...styles.selectOption,
                          backgroundColor: pet.petID === selectedPet ? '#F3F4F6' : 'transparent'
                        }}
                        onClick={() => handleSelectChange(pet.petID)}
                        onMouseOver={(e) => e.currentTarget.style.backgroundColor = '#F3F4F6'}
                        onMouseOut={(e) => e.currentTarget.style.backgroundColor = pet.petID === selectedPet ? '#F3F4F6' : 'transparent'}
                      >
                        {pet.name}
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              {error && (
                <p style={{ color: '#EA6C7B', textAlign: 'center', marginTop: '16px' }}>
                  {error}
                </p>
              )}

              {loading ? (
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', padding: '32px' }}>
                  <Loader className="h-8 w-8 text-[#EA6C7B] animate-spin" />
                  <p style={{ marginTop: '8px', color: '#6B7280' }}>Loading timeline...</p>
                </div>
              ) : !selectedPet ? (
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', padding: '32px' }}>
                  <p style={{ color: '#6B7280', textAlign: 'center' }}>
                    Please select a pet from the dropdown above to view their wellness timeline.
                  </p>
                </div>
              ) : (
                <div style={styles.timelineSection}>
                  <div style={styles.timelineHeader}>
                    <h2 style={styles.timelineTitle}>Wellness Timeline</h2>
                    <div style={styles.buttonGroup}>
                      <button style={{...styles.outlineButton, ...styles.activeButton}}>
                        Timeline
                      </button>
                    </div>
                  </div>
                  <div style={styles.timelineContainer}>
                    <div style={styles.timelineNavigation}>
                      <button 
                        style={styles.navButton}
                        onClick={() => navigateTimeline('prev')}
                        onMouseOver={(e) => e.currentTarget.style.backgroundColor = 'rgba(0, 0, 0, 0.05)'}
                        onMouseOut={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                      >
                        <ChevronLeft className="h-6 w-6" />
                      </button>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                        <span style={{ color: '#042C3C', fontWeight: '500' }}>
                          {format(visibleDates[0], "MMM d")} - {format(visibleDates[visibleDates.length - 1], "MMM d, yyyy")}
                        </span>
                        <div 
                          style={styles.calendarIcon}
                          onClick={() => setShowCalendar(!showCalendar)}
                          onMouseOver={(e) => e.currentTarget.style.backgroundColor = 'rgba(0, 0, 0, 0.05)'}
                          onMouseOut={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                        >
                          <Calendar className="h-5 w-5" />
                        </div>
                      </div>
                      <button 
                        style={styles.navButton}
                        onClick={() => navigateTimeline('next')}
                        onMouseOver={(e) => e.currentTarget.style.backgroundColor = 'rgba(0, 0, 0, 0.05)'}
                        onMouseOut={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                      >
                        <ChevronRight className="h-6 w-6" />
                      </button>
                    </div>
                    <div style={styles.timelineGrid}>
                      {visibleDates.map((date, i) => (
                        <div 
                          key={i} 
                          style={{
                            ...styles.timelineDay,
                            ...(isSameDay(date, new Date()) ? styles.timelineDayToday : {})
                          }}
                        >
                          <div style={styles.timelineDayHeader}>
                            <div style={styles.timelineDayName}>{format(date, "EEE")}</div>
                            <div style={styles.timelineDayNumber}>{format(date, "d")}</div>
                          </div>
                          <div style={{ marginTop: '12px', width: '100%' }}>
                            {getEventsForDate(date).map((event, eventIndex) => (
                              <div 
                                key={event.id}
                                style={{
                                  ...styles.timelineEvent,
                                  backgroundColor: event.color,
                                }}
                                onMouseOver={(e) => e.currentTarget.style.transform = 'scale(1.02)'}
                                onMouseOut={(e) => e.currentTarget.style.transform = 'scale(1)'}
                              >
                                <div style={styles.timelineEventTitle}>{event.title}</div>
                                <div style={styles.timelineEventType}>{event.type}</div>
                                {event.progress !== undefined && (
                                  <div style={{ 
                                    width: '100%', 
                                    height: '4px', 
                                    backgroundColor: 'rgba(255,255,255,0.3)', 
                                    marginTop: '4px',
                                    borderRadius: '2px'
                                  }}>
                                    <div style={{ 
                                      width: `${event.progress}%`, 
                                      height: '100%', 
                                      backgroundColor: 'white',
                                      borderRadius: '2px'
                                    }} />
                                  </div>
                                )}
                              </div>
                            ))}
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Sidebar */}
          <div style={styles.sidebar}>
            {/* Overall Progress */}
            <div className="relative bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden mb-6 transition-transform duration-200 hover:scale-[1.015] hover:shadow-xl">
              {/* Accent Bar */}
              <div className="absolute left-0 top-0 h-full w-2 bg-gradient-to-b from-[#EA6C7B] to-[#68D391]" />
              <div className="p-6">
                <h2 className="text-lg font-bold text-[#042C3C] mb-4 flex items-center gap-2">
                  <span>📈</span> Overall Progress
                </h2>
                <div className="mb-6">
                  <div className="flex justify-between text-xs font-medium mb-1">
                    <span>Daily plan</span>
                    <span>{timelineEvents.length > 0 
                      ? `${Math.round(timelineEvents.reduce((acc, event) => acc + (event.progress || 0), 0) / timelineEvents.length)}%`
                      : "0%"}
                    </span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-3 overflow-hidden">
                    <div
                      className="h-3 rounded-full transition-all duration-500 bg-gradient-to-r from-[#EA6C7B] to-[#68D391]"
                      style={{ width: timelineEvents.length > 0 
                        ? `${Math.round(timelineEvents.reduce((acc, event) => acc + (event.progress || 0), 0) / timelineEvents.length)}%`
                        : "0%" }}
                    ></div>
                  </div>
                </div>
                <div className="grid grid-cols-2 gap-3">
                  <div className="flex flex-col items-center">
                    <span className="text-2xl font-bold text-[#042C3C]">{timelineEvents.length}</span>
                    <span className="text-xs text-gray-500">Total Events</span>
                  </div>
                  <div className="flex flex-col items-center">
                    <span className="text-2xl font-bold text-[#042C3C]">{timelineEvents.filter(event => event.status === "COMPLETED").length}</span>
                    <span className="text-xs text-gray-500">Completed</span>
                  </div>
                </div>
              </div>
            </div>

            {/* Upcoming Events */}
            <div className="relative bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden transition-transform duration-200 hover:scale-[1.015] hover:shadow-xl">
              {/* Accent Bar */}
              <div className="absolute left-0 top-0 h-full w-2 bg-gradient-to-b from-[#68D391] to-[#EA6C7B]" />
              <div className="p-6">
                <div className="flex items-center gap-2 mb-4">
                  <span>📅</span>
                  <h2 className="text-lg font-bold text-[#042C3C] mb-0">Upcoming Events</h2>
                </div>
                <div className="flex flex-col gap-4">
                  {timelineEvents
                    .filter(event => new Date(event.startDate) >= new Date())
                    .slice(0, 3)
                    .map((event) => (
                      <div key={event.id} className="flex items-center gap-3 bg-[#FFF7EC] rounded-xl p-3">
                        <div className={`inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-semibold ${event.status === 'COMPLETED' ? 'bg-green-100 text-green-700' : event.status === 'PENDING' ? 'bg-yellow-100 text-yellow-700' : event.status === 'CANCELLED' ? 'bg-gray-200 text-gray-500' : 'bg-blue-100 text-blue-700'}`}>
                          {event.status === 'COMPLETED' ? <span>✔️</span> : event.status === 'PENDING' ? <span>⏳</span> : event.status === 'CANCELLED' ? <span>⛔</span> : <span>📋</span>}{event.type}
                        </div>
                        <div className="flex-1">
                          <h3 className="font-semibold text-[#042C3C] text-sm mb-0.5">{event.title}</h3>
                          <div className="flex items-center gap-2 text-xs text-gray-500">
                            <span>{format(new Date(event.startDate), "MMM d")}
                              {event.endDate && !isSameDay(new Date(event.startDate), new Date(event.endDate)) 
                                ? ` - ${format(new Date(event.endDate), "MMM d")}`
                                : ''}
                            </span>
                            {event.progress !== undefined && (
                              <span className="ml-2">{event.progress}%</span>
                            )}
                          </div>
                        </div>
                      </div>
                    ))}
                  {timelineEvents.filter(event => new Date(event.startDate) >= new Date()).length === 0 && (
                    <div className="text-xs text-gray-500 text-center py-4">No upcoming events.</div>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Calendar Modal */}
        {showCalendar && (
          <div style={styles.modalOverlay} onClick={() => setShowCalendar(false)}>
            <div 
              style={styles.calendarModal}
              onClick={(e) => e.stopPropagation()}
            >
              <div style={styles.calendarHeader}>
                <div style={styles.calendarNavigation}>
                  <button
                    style={styles.calendarNavButton}
                    onClick={() => setCurrentDate(subDays(currentDate, 30))}
                    onMouseOver={(e) => e.currentTarget.style.backgroundColor = 'rgba(0, 0, 0, 0.05)'}
                    onMouseOut={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                  >
                    <ChevronLeft className="h-5 w-5" />
                  </button>
                  <span style={styles.calendarTitle}>
                    {format(currentDate, "MMMM yyyy")}
                  </span>
                  <button
                    style={styles.calendarNavButton}
                    onClick={() => setCurrentDate(addDays(currentDate, 30))}
                    onMouseOver={(e) => e.currentTarget.style.backgroundColor = 'rgba(0, 0, 0, 0.05)'}
                    onMouseOut={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                  >
                    <ChevronRight className="h-5 w-5" />
                  </button>
                </div>
                <button
                  style={styles.closeButton}
                  onClick={() => setShowCalendar(false)}
                  onMouseOver={(e) => e.currentTarget.style.backgroundColor = 'rgba(0, 0, 0, 0.05)'}
                  onMouseOut={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                >
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <line x1="18" y1="6" x2="6" y2="18"></line>
                    <line x1="6" y1="6" x2="18" y2="18"></line>
                  </svg>
                </button>
              </div>
              <div style={styles.calendarGrid}>
                {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map(day => (
                  <div key={day} style={styles.calendarWeekday}>{day}</div>
                ))}
                {generateCalendarDays(currentDate).map((date, index) => (
                  <div
                    key={index}
                    style={{
                      ...styles.calendarDay,
                      ...(!date && styles.calendarDayEmpty),
                      ...(date && isSameDay(date, new Date()) && styles.calendarDayToday),
                      ...(date && selectedDate && isSameDay(date, selectedDate) && styles.calendarDaySelected)
                    }}
                    onClick={() => date && handleDateSelect(date)}
                    onMouseOver={(e) => date && (e.currentTarget.style.backgroundColor = '#F3F4F6')}
                    onMouseOut={(e) => date && (e.currentTarget.style.backgroundColor = 'transparent')}
                  >
                    {date ? format(date, "d") : ""}
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default WellnessTimeline;
