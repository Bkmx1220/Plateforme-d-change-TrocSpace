<template>
  <nav class="navbar navbar-expand-lg navbar-light fixed-top" :class="{'bg-transparent-custom': !isNightMode, 'bg-dark-custom': isNightMode}">
    <div class="container">
      <router-link to="/" class="navbar-brand text-orange font-weight-bold">TrocSpace</router-link>

      <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
          aria-controls="navbarNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
      >
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse justify-content-center" id="navbarNav">
        <ul class="navbar-nav">
          <li class="nav-item">
            <router-link to="/annonces" class="nav-link" :class="{'text-white': isNightMode, 'text-dark': !isNightMode}">Annonces</router-link>
          </li>
          <li class="nav-item">
            <router-link to="/a-propos" class="nav-link" :class="{'text-white': isNightMode, 'text-dark': !isNightMode}">À propos</router-link>
          </li>
          <li class="nav-item">
            <router-link to="/contact" class="nav-link" :class="{'text-white': isNightMode, 'text-dark': !isNightMode}">Contact</router-link>
          </li>
        </ul>
      </div>

      <ul class="navbar-nav ms-auto">
        <template v-if="!isAuthenticated">
          <li class="nav-item">
            <router-link to="/login" class="nav-link" :class="{'text-white': isNightMode, 'text-dark': !isNightMode}">Se connecter</router-link>
          </li>
          <li class="nav-item">
            <router-link to="/register" class="nav-link" :class="{'text-white': isNightMode, 'text-dark': !isNightMode}">S'inscrire</router-link>
          </li>
        </template>

        <template v-else>
          <li class="nav-item dropdown">
            <a
                class="nav-link dropdown-toggle d-flex align-items-center"
                href="#"
                id="profileDropdown"
                role="button"
                data-bs-toggle="dropdown"
                aria-expanded="false"
            >
              <img
                  :src="photoProfil || defaultAvatar"
                  alt="Photo de profil"
                  class="rounded-circle border border-2 shadow-sm"
                  width="40"
                  height="40"
              />
              <span class="ms-2" :class="{'text-white': isNightMode, 'text-dark': !isNightMode}">{{ userName }}</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="profileDropdown">
              <li>
                <router-link to="/profile" class="dropdown-item" :class="{'text-white': isNightMode, 'text-dark': !isNightMode}">Mon profil</router-link>
              </li>
              <li>
                <button @click="logout" class="dropdown-item" :class="{'text-white': isNightMode, 'text-dark': !isNightMode}">Déconnexion</button>
              </li>
            </ul>
          </li>
        </template>
      </ul>
    </div>
  </nav>
</template>

<script>
import axios from "axios";
import defaultAvatar from "@/assets/images/default_avatar.jpg";

export default {
  data() {
    return {
      isAuthenticated: false,
      userName: "",
      photoProfil: "",
      authToken: localStorage.getItem("auth_token"),
      defaultAvatar: defaultAvatar,
      isNightMode: false, // Variable pour le mode nuit
    };
  },
  mounted() {
    this.checkAuthentication();
    this.checkNightMode();
  },
  methods: {
    async checkAuthentication() {
      if (!this.authToken) {
        this.isAuthenticated = false;
        return;
      }

      try {
        const response = await axios.get("/verification/details", {
          headers: { Authorization: `Bearer ${this.authToken}` },
        });

        if (response.status === 200) {
          this.isAuthenticated = true;
          this.userName = `${response.data.nom} ${response.data.prenom}`;
          this.photoProfil = response.data.photoProfil
              ? response.data.photoProfil
              : this.defaultAvatar;
        }
      } catch (error) {
        console.error("Erreur d'authentification", error);
        this.isAuthenticated = false;
        localStorage.removeItem("auth_token");
      }
    },
    logout() {
      localStorage.removeItem("auth_token");
      window.location.href = "/";
    },
    checkNightMode() {
      // Récupérer l'état du mode nuit du stockage local (si défini)
      const storedMode = localStorage.getItem("night_mode");
      this.isNightMode = storedMode === "true";
      // Appliquer la classe pour le mode nuit
      document.body.classList.toggle("night-mode", this.isNightMode);
    },
    toggleNightMode() {
      // Inverser l'état du mode nuit et l'enregistrer dans le stockage local
      this.isNightMode = !this.isNightMode;
      localStorage.setItem("night_mode", this.isNightMode);
      document.body.classList.toggle("night-mode", this.isNightMode);
    },
  },
};
</script>

<style scoped>
.navbar-nav .nav-link {
  font-size: 1.1rem;
  padding: 5px 15px;
}

.rounded-circle {
  object-fit: cover;
}

.text-orange {
  color: #FFA500;
}

.font-weight-bold {
  font-weight: bold;
}

/* Navbar semi-transparente */
.bg-transparent-custom {
  background-color: rgba(255, 255, 255, 0.5); /* Blanc avec 80% d'opacité */
  backdrop-filter: blur(10px); /* Ajoute un flou d'arrière-plan pour un effet plus élégant */
}

/* Mode nuit */
.night-mode {
  background-color: #121212;
  color: #ffffff;
}

.bg-dark-custom {
  background-color: rgba(0, 0, 0, 0.7); /* Fond sombre avec un peu de transparence */
  backdrop-filter: blur(10px); /* Ajoute un flou pour donner un effet de profondeur */
}

.navbar-nav .nav-link, .navbar-nav .nav-item {
  transition: color 0.3s ease;
}

/* Liens du mode nuit */
.night-mode .nav-link {
  color: #ffffff;
}

.night-mode .navbar-brand {
  color: #FFA500;
}

.night-mode .dropdown-item {
  color: #ffffff;
}

/* Mode jour */
body {
  padding-top: 70px; /* Ajuster selon la hauteur réelle de la navbar */
}
</style>
