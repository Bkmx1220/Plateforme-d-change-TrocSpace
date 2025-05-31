<template>
  <LayoutSidebar>
    <div class="profile-container">
      <div class="card shadow-lg profile-card">
        <div class="card-body text-center">
          <!-- Chargement -->
          <div v-if="isLoading" class="spinner mx-auto"></div>

          <!-- Message d'erreur -->
          <div v-else-if="errorMessage">
            <p class="text-danger">{{ errorMessage }}</p>
          </div>

          <!-- Affichage des informations -->
          <div v-else>
            <div class="mb-4">
              <img
                  :src="photoProfil || defaultAvatar"
                  alt="Photo de profil"
                  class="rounded-circle border border-4 border-light shadow"
                  width="150"
                  height="150"
              />
            </div>
            <h3 v-if="user.nom && user.prenom" class="fw-bold text-dark">
              {{ user.nom }} {{ user.prenom }}
            </h3>
            <p v-else class="text-danger">Nom ou prénom non renseignés</p>
            <p v-if="user.email" class="text-muted">{{ user.email }}</p>
            <p v-else class="text-danger">Email non renseigné</p>

            <!-- Ajouter un indicateur de statut de vérification -->
            <div class="verification-status mt-3">
              <strong>Statut de vérification:</strong>
              <span v-if="user.isVerified" class="text-success">
                <i class="fas fa-check-circle"></i> Vérifié
              </span>
              <span v-else class="text-danger">
                <i class="fas fa-times-circle"></i> Non vérifié
              </span>
            </div>

            <div class="mt-4">
              <button @click="goToEditProfile" class="btn btn-primary">Modifier le profil</button>
              <button @click="logout" class="btn btn-danger ms-2">Déconnexion</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </LayoutSidebar>
</template>

<script>
import axios from "axios";
import defaultAvatar from "@/assets/images/default_avatar.jpg";
import LayoutSidebar from "@/layouts/LayoutSidebar.vue";

export default {
  components: { LayoutSidebar },
  data() {
    return {
      user: { prenom: "", nom: "", email: "", isVerified: false },
      photoProfil: "",
      isLoading: true,
      errorMessage: "",
      authToken: localStorage.getItem("auth_token"),
      defaultAvatar: defaultAvatar,
    };
  },
  mounted() {
    if (!this.authToken) {
      window.location.href = "/login";
    } else {
      this.fetchUserData();
    }
  },
  methods: {
    async fetchUserData() {
      this.isLoading = true;
      this.errorMessage = "";
      try {
        const response = await axios.get("/user/me", {
          headers: { Authorization: `Bearer ${this.authToken}` },
        });
        if (response.status === 200) {
          this.user = {
            nom: response.data.nom,
            prenom: response.data.prenom,
            email: response.data.email,
            isVerified: response.data.isVerified, // récupération du statut de vérification
          };
          this.photoProfil = response.data.photoProfil
              ? response.data.photoProfil
              : this.defaultAvatar;
        }
      } catch (error) {
        if (error.response?.status === 401) {
          window.location.href = "/login";
        } else {
          this.errorMessage = "Impossible de récupérer les informations. Veuillez réessayer.";
        }
      } finally {
        this.isLoading = false;
      }
    },
    logout() {
      localStorage.removeItem("auth_token");
      window.location.href = "/";
    },
    goToEditProfile() {
      this.$router.push("/edit-profile");
    },
  },
};
</script>

<style scoped>
.profile-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2em;
}

.profile-card {
  max-width: 600px;
  width: 100%;
  background-color: #f8f9fa;
  border-radius: 1rem;
  padding: 2rem;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

img {
  object-fit: cover;
  border: 4px solid #f8f9fa;
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-left-color: #007bff;
  border-radius: 50%;
  width: 50px;
  height: 50px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

button {
  width: 150px;
}

.verification-status {
  font-size: 1.1rem;
  margin-top: 10px;
}

.fas.fa-check-circle,
.fas.fa-times-circle {
  font-size: 1.3rem;
  margin-left: 8px;
}

.fas.fa-check-circle {
  color: #28a745;
}

.fas.fa-times-circle {
  color: #dc3545;
}
</style>
