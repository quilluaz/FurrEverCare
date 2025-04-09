"use client"

import { useState } from "react"
import {
  Clock,
  AlertCircle,
  Plus,
  Check,
  X,
  UserPlus,
  ChevronLeft,
  ChevronRight,
  Search,
  Bell,
  Menu,
} from "lucide-react"
import UserNavBar from "../components/UserNavBar"

// Combined MedicationTracker component with all sub-components
export default function MedicationTracker() {
  const [date, setDate] = useState(new Date())
  const [activeTab, setActiveTab] = useState("schedule")

  return (
    <div className="min-h-screen bg-[#FFF7EC] font-['Baloo']">
      <Header />
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="text-3xl font-bold text-[#042C3C] mb-6">Pawsome Medication Tracker</h1>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
          <Card className="lg:col-span-2 bg-white rounded-lg shadow-lg border-2 border-[#EA6C7B]">
            <CardHeader className="bg-[#EA6C7B] text-white rounded-t-lg">
              <CardTitle className="text-2xl font-bold">Upcoming Treatments</CardTitle>
            </CardHeader>
            <CardContent>
              <UpcomingTreatments />
            </CardContent>
          </Card>

          <Card className="bg-white rounded-lg shadow-lg border-2 border-[#8A973F]">
            <CardHeader className="bg-[#8A973F] text-white rounded-t-lg">
              <CardTitle className="text-2xl font-bold">Notifications</CardTitle>
            </CardHeader>
            <CardContent>
              <ul className="space-y-4">
                <li className="flex items-center space-x-3 p-3 bg-[#FFF7EC] rounded-lg">
                  <AlertCircle className="h-6 w-6 text-[#EA6C7B]" />
                  <div>
                    <p className="font-medium text-[#042C3C]">Missed Dose</p>
                    <p className="text-sm text-gray-500">Buddy's flea medication</p>
                  </div>
                </li>
                <li className="flex items-center space-x-3 p-3 bg-[#FFF7EC] rounded-lg">
                  <Clock className="h-6 w-6 text-[#F0B542]" />
                  <div>
                    <p className="font-medium text-[#042C3C]">Upcoming Treatment</p>
                    <p className="text-sm text-gray-500">Whiskers' dental cleaning</p>
                  </div>
                </li>
              </ul>
            </CardContent>
          </Card>
        </div>

        <Tabs activeTab={activeTab} setActiveTab={setActiveTab}>
          <TabsList>
            <TabsTrigger
              value="schedule"
              isActive={activeTab === "schedule"}
              onClick={() => setActiveTab("schedule")}
              activeColor="#EA6C7B"
            >
              Schedule
            </TabsTrigger>
            <TabsTrigger
              value="log"
              isActive={activeTab === "log"}
              onClick={() => setActiveTab("log")}
              activeColor="#8A973F"
            >
              Treatment Log
            </TabsTrigger>
            <TabsTrigger
              value="calendar"
              isActive={activeTab === "calendar"}
              onClick={() => setActiveTab("calendar")}
              activeColor="#F0B542"
            >
              Calendar
            </TabsTrigger>
            <TabsTrigger
              value="collaborators"
              isActive={activeTab === "collaborators"}
              onClick={() => setActiveTab("collaborators")}
              activeColor="#042C3C"
            >
              Collaborators
            </TabsTrigger>
          </TabsList>

          {activeTab === "schedule" && (
            <Card className="mt-4 bg-white rounded-lg shadow-lg border-2 border-[#EA6C7B]">
              <CardHeader className="bg-[#EA6C7B] text-white rounded-t-lg">
                <CardTitle className="text-2xl font-bold">Schedule Medication/Treatment</CardTitle>
              </CardHeader>
              <CardContent>
                <MedicationScheduleForm />
              </CardContent>
            </Card>
          )}

          {activeTab === "log" && (
            <Card className="mt-4 bg-white rounded-lg shadow-lg border-2 border-[#8A973F]">
              <CardHeader className="bg-[#8A973F] text-white rounded-t-lg">
                <CardTitle className="text-2xl font-bold">Log Treatment</CardTitle>
              </CardHeader>
              <CardContent>
                <TreatmentLogForm />
              </CardContent>
            </Card>
          )}

          {activeTab === "calendar" && (
            <Card className="mt-4 bg-white rounded-lg shadow-lg border-2 border-[#F0B542]">
              <CardHeader className="bg-[#F0B542] text-white rounded-t-lg">
                <CardTitle className="text-2xl font-bold">Treatment Calendar</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="flex flex-col lg:flex-row gap-6">
                  <Calendar
                    mode="single"
                    selected={date}
                    onSelect={setDate}
                    className="rounded-lg border-2 border-[#F0B542] p-4 bg-white"
                  />
                  <AdherenceChart />
                </div>
              </CardContent>
            </Card>
          )}

          {activeTab === "collaborators" && (
            <Card className="mt-4 bg-white rounded-lg shadow-lg border-2 border-[#042C3C]">
              <CardHeader className="bg-[#042C3C] text-white rounded-t-lg">
                <CardTitle className="text-2xl font-bold">Collaborators</CardTitle>
              </CardHeader>
              <CardContent>
                <CollaboratorsList />
              </CardContent>
            </Card>
          )}
        </Tabs>
      </main>
    </div>
  )
}

// Header Component
function Header() {
  return (
  <UserNavBar />
  )
}

// Card Components
function Card({ children, className }) {
  return <div className={className}>{children}</div>
}

function CardHeader({ children, className }) {
  return <div className={`p-4 ${className}`}>{children}</div>
}

function CardTitle({ children, className }) {
  return <h2 className={className}>{children}</h2>
}

function CardContent({ children }) {
  return <div className="p-4">{children}</div>
}

// Tabs Components
function Tabs({ children }) {
  return <div className="mt-8">{children}</div>
}

function TabsList({ children }) {
  return <div className="bg-white rounded-full p-2 space-x-2 flex flex-wrap">{children}</div>
}

function TabsTrigger({ value, isActive, onClick, children, activeColor }) {
  return (
    <button
      onClick={onClick}
      className={`rounded-full px-4 py-2 transition-colors ${
        isActive ? `bg-[${activeColor}] text-white` : "text-[#042C3C] hover:bg-gray-100"
      }`}
    >
      {children}
    </button>
  )
}

// Calendar Component
function Calendar({ selected, onSelect }) {
  const [currentMonth, setCurrentMonth] = useState(new Date())

  const daysInMonth = new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 0).getDate()

  const firstDayOfMonth = new Date(currentMonth.getFullYear(), currentMonth.getMonth(), 1).getDay()

  const monthName = currentMonth.toLocaleString("default", { month: "long" })
  const year = currentMonth.getFullYear()

  const prevMonth = () => {
    setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() - 1, 1))
  }

  const nextMonth = () => {
    setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 1))
  }

  // Generate days for the calendar
  const days = []
  for (let i = 0; i < firstDayOfMonth; i++) {
    days.push(<div key={`empty-${i}`} className="h-10 w-10"></div>)
  }

  for (let day = 1; day <= daysInMonth; day++) {
    const date = new Date(currentMonth.getFullYear(), currentMonth.getMonth(), day)
    const isSelected =
      selected &&
      date.getDate() === selected.getDate() &&
      date.getMonth() === selected.getMonth() &&
      date.getFullYear() === selected.getFullYear()

    // Add some mock treatment days
    const hasTreatment = [5, 12, 19, 26].includes(day)

    days.push(
      <button
        key={day}
        onClick={() => onSelect(date)}
        className={`h-10 w-10 rounded-full flex items-center justify-center relative ${
          isSelected ? "bg-[#F0B542] text-white" : hasTreatment ? "bg-[#F0B542]/20" : "hover:bg-gray-100"
        }`}
      >
        {day}
        {hasTreatment && !isSelected && (
          <span className="absolute bottom-1 left-1/2 transform -translate-x-1/2 w-1 h-1 bg-[#F0B542] rounded-full"></span>
        )}
      </button>,
    )
  }

  return (
    <div className="w-full max-w-sm">
      <div className="flex items-center justify-between mb-4">
        <button onClick={prevMonth} className="p-1 rounded-full hover:bg-gray-100">
          <ChevronLeft className="h-5 w-5" />
        </button>
        <h2 className="font-medium">
          {monthName} {year}
        </h2>
        <button onClick={nextMonth} className="p-1 rounded-full hover:bg-gray-100">
          <ChevronRight className="h-5 w-5" />
        </button>
      </div>

      <div className="grid grid-cols-7 gap-1 mb-2">
        {["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"].map((day) => (
          <div key={day} className="h-10 flex items-center justify-center text-sm text-gray-500">
            {day}
          </div>
        ))}
      </div>

      <div className="grid grid-cols-7 gap-1">{days}</div>
    </div>
  )
}

// MedicationScheduleForm Component
function MedicationScheduleForm() {
  return (
    <form className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label className="block text-sm font-medium text-[#042C3C] mb-1">Pet</label>
          <select className="w-full p-2 border-2 border-gray-200 rounded-lg focus:border-[#EA6C7B] focus:outline-none">
            <option>Buddy (Dog)</option>
            <option>Whiskers (Cat)</option>
            <option>Thumper (Rabbit)</option>
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-[#042C3C] mb-1">Medication/Treatment</label>
          <select className="w-full p-2 border-2 border-gray-200 rounded-lg focus:border-[#EA6C7B] focus:outline-none">
            <option>Flea & Tick Prevention</option>
            <option>Heartworm Medication</option>
            <option>Antibiotics</option>
            <option>Dental Cleaning</option>
            <option>Vaccination</option>
            <option>Other</option>
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-[#042C3C] mb-1">Start Date</label>
          <input
            type="date"
            className="w-full p-2 border-2 border-gray-200 rounded-lg focus:border-[#EA6C7B] focus:outline-none"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-[#042C3C] mb-1">Frequency</label>
          <select className="w-full p-2 border-2 border-gray-200 rounded-lg focus:border-[#EA6C7B] focus:outline-none">
            <option>Daily</option>
            <option>Weekly</option>
            <option>Monthly</option>
            <option>Every 3 Months</option>
            <option>Custom</option>
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-[#042C3C] mb-1">Time</label>
          <input
            type="time"
            className="w-full p-2 border-2 border-gray-200 rounded-lg focus:border-[#EA6C7B] focus:outline-none"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-[#042C3C] mb-1">Reminder</label>
          <select className="w-full p-2 border-2 border-gray-200 rounded-lg focus:border-[#EA6C7B] focus:outline-none">
            <option>15 minutes before</option>
            <option>30 minutes before</option>
            <option>1 hour before</option>
            <option>1 day before</option>
          </select>
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium text-[#042C3C] mb-1">Notes</label>
        <textarea
          className="w-full p-2 border-2 border-gray-200 rounded-lg focus:border-[#EA6C7B] focus:outline-none"
          rows="3"
          placeholder="Add any special instructions or notes..."
        ></textarea>
      </div>

      <div className="flex justify-end">
        <button
          type="submit"
          className="px-6 py-2 bg-[#EA6C7B] text-white rounded-lg hover:bg-[#EA6C7B]/90 transition-colors"
        >
          Schedule Treatment
        </button>
      </div>
    </form>
  )
}

// TreatmentLogForm Component
function TreatmentLogForm() {
  return (
    <form className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label className="block text-sm font-medium text-[#042C3C] mb-1">Pet</label>
          <select className="w-full p-2 border-2 border-gray-200 rounded-lg focus:border-[#8A973F] focus:outline-none">
            <option>Buddy (Dog)</option>
            <option>Whiskers (Cat)</option>
            <option>Thumper (Rabbit)</option>
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-[#042C3C] mb-1">Medication/Treatment</label>
          <select className="w-full p-2 border-2 border-gray-200 rounded-lg focus:border-[#8A973F] focus:outline-none">
            <option>Flea & Tick Prevention</option>
            <option>Heartworm Medication</option>
            <option>Antibiotics</option>
            <option>Dental Cleaning</option>
            <option>Vaccination</option>
            <option>Other</option>
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-[#042C3C] mb-1">Date</label>
          <input
            type="date"
            className="w-full p-2 border-2 border-gray-200 rounded-lg focus:border-[#8A973F] focus:outline-none"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-[#042C3C] mb-1">Time</label>
          <input
            type="time"
            className="w-full p-2 border-2 border-gray-200 rounded-lg focus:border-[#8A973F] focus:outline-none"
          />
        </div>

        <div className="md:col-span-2">
          <label className="block text-sm font-medium text-[#042C3C] mb-1">Status</label>
          <div className="flex gap-4">
            <label className="flex items-center gap-2">
              <input type="radio" name="status" className="text-[#8A973F]" defaultChecked />
              <span>Completed</span>
            </label>
            <label className="flex items-center gap-2">
              <input type="radio" name="status" className="text-[#EA6C7B]" />
              <span>Missed</span>
            </label>
            <label className="flex items-center gap-2">
              <input type="radio" name="status" className="text-[#F0B542]" />
              <span>Rescheduled</span>
            </label>
          </div>
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium text-[#042C3C] mb-1">Notes</label>
        <textarea
          className="w-full p-2 border-2 border-gray-200 rounded-lg focus:border-[#8A973F] focus:outline-none"
          rows="3"
          placeholder="Add any observations or notes about the treatment..."
        ></textarea>
      </div>

      <div className="flex justify-end">
        <button
          type="submit"
          className="px-6 py-2 bg-[#8A973F] text-white rounded-lg hover:bg-[#8A973F]/90 transition-colors"
        >
          Log Treatment
        </button>
      </div>
    </form>
  )
}

// AdherenceChart Component
function AdherenceChart() {
  return (
    <div className="flex-1 bg-white p-4 rounded-lg border-2 border-[#F0B542]">
      <h3 className="text-lg font-medium text-[#042C3C] mb-4">Monthly Adherence</h3>

      <div className="space-y-4">
        <div>
          <div className="flex justify-between mb-1">
            <span className="text-sm font-medium">Buddy</span>
            <span className="text-sm text-gray-500">85%</span>
          </div>
          <div className="w-full bg-gray-200 rounded-full h-2.5">
            <div className="bg-[#EA6C7B] h-2.5 rounded-full" style={{ width: "85%" }}></div>
          </div>
        </div>

        <div>
          <div className="flex justify-between mb-1">
            <span className="text-sm font-medium">Whiskers</span>
            <span className="text-sm text-gray-500">92%</span>
          </div>
          <div className="w-full bg-gray-200 rounded-full h-2.5">
            <div className="bg-[#8A973F] h-2.5 rounded-full" style={{ width: "92%" }}></div>
          </div>
        </div>

        <div>
          <div className="flex justify-between mb-1">
            <span className="text-sm font-medium">Thumper</span>
            <span className="text-sm text-gray-500">78%</span>
          </div>
          <div className="w-full bg-gray-200 rounded-full h-2.5">
            <div className="bg-[#F0B542] h-2.5 rounded-full" style={{ width: "78%" }}></div>
          </div>
        </div>
      </div>

      <div className="mt-6">
        <h4 className="text-sm font-medium text-[#042C3C] mb-2">Treatment Breakdown</h4>
        <div className="flex gap-4">
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-green-500"></div>
            <span className="text-xs">Completed</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-red-500"></div>
            <span className="text-xs">Missed</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-yellow-500"></div>
            <span className="text-xs">Rescheduled</span>
          </div>
        </div>
      </div>
    </div>
  )
}

// UpcomingTreatments Component
function UpcomingTreatments() {
  const treatments = [
    {
      id: 1,
      pet: "Buddy",
      petType: "Dog",
      treatment: "Flea & Tick Prevention",
      date: "Today",
      time: "6:00 PM",
      status: "upcoming",
    },
    {
      id: 2,
      pet: "Whiskers",
      petType: "Cat",
      treatment: "Dental Cleaning",
      date: "Tomorrow",
      time: "10:00 AM",
      status: "upcoming",
    },
    {
      id: 3,
      pet: "Buddy",
      petType: "Dog",
      treatment: "Heartworm Medication",
      date: "May 15, 2023",
      time: "8:00 AM",
      status: "upcoming",
    },
    {
      id: 4,
      pet: "Thumper",
      petType: "Rabbit",
      treatment: "Nail Trimming",
      date: "May 18, 2023",
      time: "3:30 PM",
      status: "upcoming",
    },
  ]

  return (
    <div className="space-y-4">
      {treatments.map((treatment) => (
        <div
          key={treatment.id}
          className="flex items-center justify-between p-4 bg-[#FFF7EC] rounded-lg border-l-4 border-[#EA6C7B]"
        >
          <div className="flex items-center gap-4">
            <div className="w-10 h-10 bg-[#EA6C7B]/20 rounded-full flex items-center justify-center text-[#EA6C7B]">
              {treatment.pet.charAt(0)}
            </div>
            <div>
              <h3 className="font-medium text-[#042C3C]">{treatment.treatment}</h3>
              <p className="text-sm text-gray-500">
                {treatment.pet} ({treatment.petType})
              </p>
            </div>
          </div>
          <div className="text-right">
            <div className="font-medium text-[#042C3C]">{treatment.date}</div>
            <div className="text-sm text-gray-500">{treatment.time}</div>
          </div>
          <div className="flex gap-2">
            <button className="p-2 bg-green-100 text-green-700 rounded-full hover:bg-green-200">
              <Check className="h-4 w-4" />
            </button>
            <button className="p-2 bg-red-100 text-red-700 rounded-full hover:bg-red-200">
              <X className="h-4 w-4" />
            </button>
          </div>
        </div>
      ))}

      <button className="w-full p-3 bg-white border-2 border-dashed border-[#EA6C7B] rounded-lg text-[#EA6C7B] flex items-center justify-center gap-2 hover:bg-[#EA6C7B]/5 transition-colors">
        <Plus className="h-4 w-4" />
        <span>Add New Treatment</span>
      </button>
    </div>
  )
}

// CollaboratorsList Component
function CollaboratorsList() {
  const collaborators = [
    {
      id: 1,
      name: "John Doe",
      email: "john@example.com",
      role: "Owner",
      pets: ["Buddy", "Whiskers"],
    },
    {
      id: 2,
      name: "Jane Smith",
      email: "jane@example.com",
      role: "Veterinarian",
      pets: ["Buddy", "Whiskers", "Thumper"],
    },
    {
      id: 3,
      name: "Mike Johnson",
      email: "mike@example.com",
      role: "Pet Sitter",
      pets: ["Buddy"],
    },
  ]

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div className="relative w-64">
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <Search className="h-4 w-4 text-gray-400" />
          </div>
          <input
            type="text"
            className="pl-10 pr-4 py-2 w-full border-2 border-gray-200 rounded-lg focus:border-[#042C3C] focus:outline-none"
            placeholder="Search collaborators..."
          />
        </div>
        <button className="px-4 py-2 bg-[#042C3C] text-white rounded-lg hover:bg-[#042C3C]/90 transition-colors flex items-center gap-2">
          <UserPlus className="h-4 w-4" />
          <span>Invite</span>
        </button>
      </div>

      <div className="space-y-4">
        {collaborators.map((collaborator) => (
          <div
            key={collaborator.id}
            className="flex items-center justify-between p-4 bg-white border border-gray-200 rounded-lg hover:shadow-md transition-shadow"
          >
            <div className="flex items-center gap-4">
              <div className="w-10 h-10 bg-[#042C3C] rounded-full flex items-center justify-center text-white">
                {collaborator.name
                  .split(" ")
                  .map((n) => n[0])
                  .join("")}
              </div>
              <div>
                <h3 className="font-medium text-[#042C3C]">{collaborator.name}</h3>
                <p className="text-sm text-gray-500">{collaborator.email}</p>
              </div>
            </div>
            <div className="flex flex-col items-end">
              <span className="inline-block px-2 py-1 bg-[#042C3C]/10 text-[#042C3C] text-xs rounded-full mb-1">
                {collaborator.role}
              </span>
              <div className="text-xs text-gray-500">Access to: {collaborator.pets.join(", ")}</div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

