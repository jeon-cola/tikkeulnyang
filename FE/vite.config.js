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
      // npm run dev 모드에서의 PWA 동작: off
      devOptions: {
        enabled: false,
      },
      workbox: {
        globPatterns: ["**/*.{js,css,html,ico,png,svg,jpg}"],
        // SPA 라우팅 fallback
        navigateFallback: "/index.html",
        // /api/는 백엔드 요청 → fallback 제외
        navigateFallbackDenylist: [/^\/api\//],
        // 새 워커가 설치되면 즉시 교체
        skipWaiting: true,
        // 새 워커가 설치되면 모든 탭에 적용
        clientsClaim: true,
      },
      // 파일 이름에 해시를 붙여 SW 충돌 방지
      useFilenameHash: true,
      /* manifest 설정(아이콘 등) 필요 시 여기에 추가 */
    }),
  ],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
    extensions: [".js", ".jsx"],
  },
  build: {
    rollupOptions: {
      onwarn(warning, warn) {
        // 특정 경고 무시
        if (warning.code === "UNRESOLVED_IMPORT") return;
        warn(warning);
      },
    },
  },
});
