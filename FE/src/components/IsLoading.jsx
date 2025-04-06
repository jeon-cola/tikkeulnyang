// components/IsLoading.tsx
'use client'

import { motion } from 'framer-motion'
import { Loader2 } from 'lucide-react'  // lucide 아이콘 사용

export default function IsLoading() {
  return (
    <div className="flex items-center justify-center h-screen bg-white">
      <motion.div
        initial={{ scale: 1, rotate: 0, opacity: 1 }}
        animate={{ scale: 0, rotate: 360, opacity: 0 }}
        transition={{
          duration: 1.2,
          ease: 'easeInOut',
          repeat: Infinity,
          repeatType: 'loop'
        }}
      >
        <Loader2 className="w-12 h-12 text-blue-500 animate-spin" />
      </motion.div>
    </div>
  )
}
