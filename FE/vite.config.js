import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import svgr from "vite-plugin-svgr";
import tailwindcss from "@tailwindcss/vite";
import { fileURLToPath, URL } from "node:url";
import { VitePWA } from "vite-plugin-pwa";

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
    svgr(),
    VitePWA({
      registerType: "autoUpdate",
      // npm run dev모드에서도 pwa환경과 동일하게 설정
      devOptions: {
        enabled: true,
      },
      // pwa 다운로드후 오프라인인 상태에서도 표시
      workbox: {
        globPatterns: ["**/*.{js,css,html,ico,png,svg,jpg}"],
      },
      /* manifest 관련 설정 -> 아이콘 등이 나오면 하는걸로
      includeAssets: ['favicon.ico'],
      manifest: {
        name: '티끌냥',
        short_name: '티끌냥',
        description: '앱에 대한 간단한 설명',
        theme_color: '#FF957A',
        icons: [
          {
            src: '/pwa-192x192.png',
            sizes: '192x192',
            type: 'image/png'
          },
        ]
      },*/
    }),
  ],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
});
