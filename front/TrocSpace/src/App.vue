<template>
  <div id="app">

    <!-- Routes de l'application -->
    <router-view />
  </div>
</template>

<script>
// Importer le composant Navbar
import Navbar from './components/Navbar.vue';

export default {
  components: {
    Navbar
  }
};
</script>

<style>
/* Ajoutez vos styles globaux ici */
</style>
