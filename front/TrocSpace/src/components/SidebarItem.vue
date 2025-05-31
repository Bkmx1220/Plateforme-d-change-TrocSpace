<template>
  <li>
    <a :href="item.href" :class="{ active: item.active, 'dark-mode': isDarkMode }" class="sidebar-item">
      <svg class="icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor">
        <g v-html="item.icon"></g>
      </svg>
      <span class="label">{{ item.label }}</span>
    </a>
  </li>
</template>

<script>
export default {
  name: "SidebarItem",
  props: {
    item: Object,
  },
  data() {
    return {
      isDarkMode: false,
    };
  },
  mounted() {
    // Vérifier si le mode sombre est activé au chargement
    this.isDarkMode = localStorage.getItem('theme') === 'dark' || (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches);

    // Listener pour écouter les changements de préférence de thème
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
      this.isDarkMode = e.matches;
      localStorage.setItem('theme', this.isDarkMode ? 'dark' : 'light');
    });
  },
};
</script>

<style scoped>
.sidebar-item {
  text-decoration: none;
  display: flex;
  align-items: center;
  color: #333;
  padding: 0.75rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s, color 0.3s;
}

.sidebar-item:hover {
  background-color: #f1f1f1;
}

.sidebar-item.active {
  background-color: #007bff;
  color: white;
}

/* Styles pour le mode sombre */
.sidebar-item.dark-mode {
  color: #f0f0f0;
}

.sidebar-item.dark-mode:hover {
  background-color: #444;
}

.sidebar-item.dark-mode.active {
  background-color: #007bff;
  color: #f0f0f0;
}

.icon {
  margin-right: 10px;
  fill: currentColor;
}

.label {
  font-size: 1rem;
}

/* Définir des couleurs spécifiques pour les modes clair et sombre */
body {
  padding-top: 70px; /* Ajuster selon la hauteur réelle de la navbar */
}

body[data-theme='dark'] {
  background-color: #121212;
  color: #f0f0f0;
}

body[data-theme='light'] {
  background-color: #ffffff;
  color: #333;
}
</style>
