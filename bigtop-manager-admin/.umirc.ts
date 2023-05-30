import { defineConfig } from "umi";

export default defineConfig({
  routes: [
    { path: "/", component: "index" },
    { path: "/docs", component: "docs" },
    { path: "/login", component: "login", layout: false }
  ],
  npmClient: 'pnpm',
});
