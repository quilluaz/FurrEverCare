/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'primary': '#042C3C',
        'secondary': '#EA6C7B',
        'accent': '#8A973F',
        'highlight': '#F0B542',
        'background': '#FFF7EC',
      },
      fontFamily: {
        'baloo': ['Baloo', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
