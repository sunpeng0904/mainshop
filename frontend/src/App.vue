<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  overflow-x: hidden;
}

a {
  text-decoration: none;
  color: inherit;
}

ul, ol {
  list-style: none;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 通用类 */
.text-center {
  text-align: center;
}

.text-right {
  text-align: right;
}

.text-left {
  text-align: left;
}

.mt-10 { margin-top: 10px; }
.mt-20 { margin-top: 20px; }
.mt-30 { margin-top: 30px; }
.mb-10 { margin-bottom: 10px; }
.mb-20 { margin-bottom: 20px; }
.mb-30 { margin-bottom: 30px; }
.ml-10 { margin-left: 10px; }
.ml-20 { margin-left: 20px; }
.mr-10 { margin-right: 10px; }
.mr-20 { margin-right: 20px; }

.p-10 { padding: 10px; }
.p-20 { padding: 20px; }
.p-30 { padding: 30px; }

.flex {
  display: flex;
}

.flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

.flex-between {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.flex-around {
  display: flex;
  align-items: center;
  justify-content: space-around;
}

.flex-column {
  flex-direction: column;
}

.flex-wrap {
  flex-wrap: wrap;
}

/* 动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .hidden-mobile {
    display: none !important;
  }
}

@media (min-width: 769px) {
  .hidden-desktop {
    display: none !important;
  }
}
</style>