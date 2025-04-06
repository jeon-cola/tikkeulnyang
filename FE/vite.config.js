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
      /* manifest 설정(아이콘 등). 해당 정보들이 있어야 PWA로 다운로드 및 오프라인 노출이 가능해진다. */
      manifest: {
        name: "TIKKUEL",
        short_name: "TIKKUEL",
        description: "가계부와 챌린지를 통한 절약 유도 앱",
        theme_color: "#FF957A",
        background_color: "#ffffff",
        display: "standalone",
        icons: [
          {
            src: "/icons/logo_tmp_192.png",
            sizes: "192x192",
            type: "image/png",
          },
          {
            src: "/icons/logo_tmp_512.png",
            sizes: "512x512",
            type: "image/png",
          },
          {
            src: "/icons/logo_tmp_512.png",
            sizes: "512x512",
            type: "image/png",
            purpose: "maskable",
          },
        ],
      },
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
