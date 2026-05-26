// vite.config.js
import { defineConfig } from "file:///C:/Users/YunH/Desktop/%E5%9F%BA%E4%BA%8ESpring%20Boot%E7%9A%84%E6%8B%9B%E8%81%98%E7%AE%A1%E7%90%86%E7%B3%BB%E7%BB%9F/frontend/node_modules/vite/dist/node/index.js";
import vue from "file:///C:/Users/YunH/Desktop/%E5%9F%BA%E4%BA%8ESpring%20Boot%E7%9A%84%E6%8B%9B%E8%81%98%E7%AE%A1%E7%90%86%E7%B3%BB%E7%BB%9F/frontend/node_modules/@vitejs/plugin-vue/dist/index.mjs";
import UnoCSS from "file:///C:/Users/YunH/Desktop/%E5%9F%BA%E4%BA%8ESpring%20Boot%E7%9A%84%E6%8B%9B%E8%81%98%E7%AE%A1%E7%90%86%E7%B3%BB%E7%BB%9F/frontend/node_modules/unocss/dist/vite.mjs";
import { resolve } from "path";
var __vite_injected_original_dirname = "C:\\Users\\YunH\\Desktop\\\u57FA\u4E8ESpring Boot\u7684\u62DB\u8058\u7BA1\u7406\u7CFB\u7EDF\\frontend";
var vite_config_default = defineConfig({
  plugins: [
    vue(),
    UnoCSS()
  ],
  resolve: {
    alias: {
      "@": resolve(__vite_injected_original_dirname, "src")
    }
  },
  server: {
    port: 5173,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true
      },
      "/uploads": {
        target: "http://localhost:8080",
        changeOrigin: true
      }
    }
  }
});
export {
  vite_config_default as default
};
//# sourceMappingURL=data:application/json;base64,ewogICJ2ZXJzaW9uIjogMywKICAic291cmNlcyI6IFsidml0ZS5jb25maWcuanMiXSwKICAic291cmNlc0NvbnRlbnQiOiBbImNvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9kaXJuYW1lID0gXCJDOlxcXFxVc2Vyc1xcXFxZdW5IXFxcXERlc2t0b3BcXFxcXHU1N0ZBXHU0RThFU3ByaW5nIEJvb3RcdTc2ODRcdTYyREJcdTgwNThcdTdCQTFcdTc0MDZcdTdDRkJcdTdFREZcXFxcZnJvbnRlbmRcIjtjb25zdCBfX3ZpdGVfaW5qZWN0ZWRfb3JpZ2luYWxfZmlsZW5hbWUgPSBcIkM6XFxcXFVzZXJzXFxcXFl1bkhcXFxcRGVza3RvcFxcXFxcdTU3RkFcdTRFOEVTcHJpbmcgQm9vdFx1NzY4NFx1NjJEQlx1ODA1OFx1N0JBMVx1NzQwNlx1N0NGQlx1N0VERlxcXFxmcm9udGVuZFxcXFx2aXRlLmNvbmZpZy5qc1wiO2NvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9pbXBvcnRfbWV0YV91cmwgPSBcImZpbGU6Ly8vQzovVXNlcnMvWXVuSC9EZXNrdG9wLyVFNSU5RiVCQSVFNCVCQSU4RVNwcmluZyUyMEJvb3QlRTclOUElODQlRTYlOEIlOUIlRTglODElOTglRTclQUUlQTElRTclOTAlODYlRTclQjMlQkIlRTclQkIlOUYvZnJvbnRlbmQvdml0ZS5jb25maWcuanNcIjtpbXBvcnQgeyBkZWZpbmVDb25maWcgfSBmcm9tICd2aXRlJ1xuaW1wb3J0IHZ1ZSBmcm9tICdAdml0ZWpzL3BsdWdpbi12dWUnXG5pbXBvcnQgVW5vQ1NTIGZyb20gJ3Vub2Nzcy92aXRlJ1xuaW1wb3J0IHsgcmVzb2x2ZSB9IGZyb20gJ3BhdGgnXG5cbmV4cG9ydCBkZWZhdWx0IGRlZmluZUNvbmZpZyh7XG4gIHBsdWdpbnM6IFtcbiAgICB2dWUoKSxcbiAgICBVbm9DU1MoKSxcbiAgXSxcbiAgcmVzb2x2ZToge1xuICAgIGFsaWFzOiB7XG4gICAgICAnQCc6IHJlc29sdmUoX19kaXJuYW1lLCAnc3JjJyksXG4gICAgfSxcbiAgfSxcbiAgc2VydmVyOiB7XG4gICAgcG9ydDogNTE3MyxcbiAgICBwcm94eToge1xuICAgICAgJy9hcGknOiB7XG4gICAgICAgIHRhcmdldDogJ2h0dHA6Ly9sb2NhbGhvc3Q6ODA4MCcsXG4gICAgICAgIGNoYW5nZU9yaWdpbjogdHJ1ZSxcbiAgICAgIH0sXG4gICAgICAnL3VwbG9hZHMnOiB7XG4gICAgICAgIHRhcmdldDogJ2h0dHA6Ly9sb2NhbGhvc3Q6ODA4MCcsXG4gICAgICAgIGNoYW5nZU9yaWdpbjogdHJ1ZSxcbiAgICAgIH0sXG4gICAgfSxcbiAgfSxcbn0pXG4iXSwKICAibWFwcGluZ3MiOiAiO0FBQWlhLFNBQVMsb0JBQW9CO0FBQzliLE9BQU8sU0FBUztBQUNoQixPQUFPLFlBQVk7QUFDbkIsU0FBUyxlQUFlO0FBSHhCLElBQU0sbUNBQW1DO0FBS3pDLElBQU8sc0JBQVEsYUFBYTtBQUFBLEVBQzFCLFNBQVM7QUFBQSxJQUNQLElBQUk7QUFBQSxJQUNKLE9BQU87QUFBQSxFQUNUO0FBQUEsRUFDQSxTQUFTO0FBQUEsSUFDUCxPQUFPO0FBQUEsTUFDTCxLQUFLLFFBQVEsa0NBQVcsS0FBSztBQUFBLElBQy9CO0FBQUEsRUFDRjtBQUFBLEVBQ0EsUUFBUTtBQUFBLElBQ04sTUFBTTtBQUFBLElBQ04sT0FBTztBQUFBLE1BQ0wsUUFBUTtBQUFBLFFBQ04sUUFBUTtBQUFBLFFBQ1IsY0FBYztBQUFBLE1BQ2hCO0FBQUEsTUFDQSxZQUFZO0FBQUEsUUFDVixRQUFRO0FBQUEsUUFDUixjQUFjO0FBQUEsTUFDaEI7QUFBQSxJQUNGO0FBQUEsRUFDRjtBQUNGLENBQUM7IiwKICAibmFtZXMiOiBbXQp9Cg==
