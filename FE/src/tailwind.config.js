/** @type {import('tailwindcss').Config} */
export default {
  content: ["./src/**/*.{html,js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: {
          50: "#FFF0EC",
          100: "#FFE0D9",
          200: "#FFC7B8",
          300: "#FFAE97",
          400: "#FFA188",
          500: "#FF957A",
          600: "#FF7B5C",
          700: "#FF613E",
          800: "#FF4720",
          900: "#E53000",
        },
        //       secondary: {
        //         50 : '#FEFDF1',
        //         100: '#FEF3C7',
        //         200: '#FDE68A',
        //         300: '#FCD34D',
        //         400: '#FBBF24',
        //         500: '#F59E0B',
        //         600: '#D97706',
        //         700: '#B45309',
        //         800: '#92400E',
        //         900: '#78350F',
        //       },
        //       neutral: {
        //         100: '#F3F4F6',
        //         200: '#E5E7EB',
        //         300: '#D1D5DB',
        //         400: '#9CA3AF',
        //         500: '#6B7280',
        //         600: '#4B5563',
        //         700: '#374151',
        //         800: '#1F2937',
        //         900: '#111827',
        //       }
      },
    },
  },
  plugins: [],
};
