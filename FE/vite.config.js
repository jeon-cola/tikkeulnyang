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
      // npm run dev모드에서도 pwa환경과 동일하게 설정 -> ignore에도 dev-dist폴더가 먹히지를 않아서, 주석처리한다.
      // devOptions: {
      //   enabled: true,
      // },
      // pwa 다운로드후 오프라인인 상태에서도 표시
      workbox: {
        globPatterns: ["**/*.{js,css,html,ico,png,svg,jpg}"],

        // 1) SPA 라우팅용 navigateFallback
        navigateFallback: "/index.html",

        // 2) /api/ 경로는 fallback에서 제외시킴
        //    즉, /api/ 요청은 서버로 그대로 가게 함
        navigateFallbackDenylist: [
          // 정규식: ^/api/ 로 시작하는 모든 요청
          /^\/api\//,
        ],
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
    extensions: [".js", ".jsx"],
  },
  build: {
    rollupOptions: {
      onwarn(warning, warn) {
        // "경로를 찾을 수 없음" 오류만 무시하고 나머지는 출력
        if (warning.code === "UNRESOLVED_IMPORT") return;
        warn(warning);
      },
    },
  },
});
