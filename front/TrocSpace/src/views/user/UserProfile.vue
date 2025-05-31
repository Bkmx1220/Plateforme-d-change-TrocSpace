<template>
  <LayoutSidebar>
    <div class="container-fluid mt-4">
      <div class="card shadow-lg" style="max-width: 80em; width: 100%; margin: auto;">
        <div class="card-body">
          <h4 class="card-title text-center mb-4">Profil de l'utilisateur</h4>

          <!-- Chargement des données -->
          <div v-if="isLoading" class="spinner-border text-primary" role="status">
            <span class="sr-only">Chargement...</span>
          </div>

          <!-- Message d'erreur -->
          <div v-else-if="errorMessage" class="alert alert-danger">
            {{ errorMessage }}
          </div>

          <!-- Affichage des informations utilisateur -->
          <div v-else>
            <div class="row">
              <div class="col-md-4 text-center">
                <img
                    :src="user.photoProfil ? user.photoProfil : defaultAvatar"
                    alt="Photo de profil"
                    class="img-fluid rounded-circle"
                    style="width: 150px; height: 150px; object-fit: cover;"
                />
              </div>
              <div class="col-md-8">
                <div class="user-info">
                  <h5>{{ user.nom }} {{ user.prenom }}</h5>
                  <p><strong>Email:</strong> {{ user.email }}</p>

                  <p><strong>Téléphone:</strong> {{ user.telephone }}</p>


                  <!-- Indicateur de vérification -->
                  <div class="verification-status mb-3">
                    <strong>Vérification du compte:</strong>
                    <span v-if="user.isVerified" class="text-success">
                      <i class="fas fa-check-circle" title="Compte vérifié"></i> Vérifié
                    </span>
                    <span v-else class="text-danger">
                      <i class="fas fa-times-circle" title="Compte non vérifié"></i> Non vérifié
                    </span>
                  </div>

                  <!-- Afficher des informations supplémentaires seulement si l'utilisateur est admin -->
                  <div v-if="isAdmin">
                    <p>
                      <strong>Statut:</strong>
                      <span :class="user.enabled ? 'text-success' : 'text-danger'">
                      {{ user.enabled ? 'Activé' : 'Désactivé' }}
                    </span>
                    </p>
                    <p><strong>Rôle:</strong> {{ user.role }}</p>
                    <!-- Ajouter selfie et photo de carte d'identité si l'utilisateur est admin -->
                    <div v-if="user.selfieCarteIdentite">
                      <p><strong>Carte d'identité:</strong>
                        <img :src="user.selfieCarteIdentite" alt="Carte d'identité" class="img-fluid rounded" />
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <button class="btn btn-primary mt-3" @click="goBack">Retour à la liste</button>
          </div>
        </div>
      </div>
    </div>
  </LayoutSidebar>
</template>

<script>
import axios from "axios";
import LayoutSidebar from "@/layouts/LayoutSidebar.vue";
import defaultAvatar from "@/assets/images/default_avatar.jpg";

export default {
  components: { LayoutSidebar },
  data() {
    return {
      user: {},
      isLoading: true,
      errorMessage: "",
      authToken: localStorage.getItem("auth_token"),
      isAdmin: false,
      defaultAvatar: defaultAvatar,
    };
  },
  mounted() {
    if (!this.authToken) {
      window.location.href = "/login";  // Redirection si non authentifié
    } else {
      this.fetchUserRole();
      this.fetchUserData();
    }
  },
  methods: {
    // Récupérer le rôle de l'utilisateur
    async fetchUserRole() {
      try {
        const response = await axios.get("http://localhost:8080/api/user/me", {
          headers: { Authorization: `Bearer ${this.authToken}` },
        });
        this.isAdmin = response.data.role === "ADMIN";  // Détermine si l'utilisateur est admin
      } catch (error) {
        console.error("Erreur dans fetchUserRole :", error);
        this.errorMessage = "Erreur lors de la récupération des informations de rôle.";
      }
    },
    // Récupérer les informations de l'utilisateur
    async fetchUserData() {
      const userId = this.$route.params.id;
      try {
        const response = await axios.get(`http://localhost:8080/api/user/${userId}`, {
          headers: { Authorization: `Bearer ${this.authToken}` },
        });
        this.user = response.data;
      } catch (error) {
        console.error("Erreur dans fetchUserData :", error);
        this.errorMessage = "Impossible de récupérer les informations de l'utilisateur.";
      } finally {
        this.isLoading = false;
      }
    },
    // Retourner à la liste des utilisateurs
    goBack() {
      this.$router.push("/admin/users");
    },
  },
};
</script>

<style scoped>
.container-fluid {
  max-width: 100%;
  padding: 0;
}

.card {
  border-radius: 15px;
}

.card-body {
  padding: 2rem;
}

h4.card-title {
  font-size: 1.5rem;
}

.user-info {
  margin-top: 20px;
}

.user-info p {
  font-size: 1.1rem;
  margin-bottom: 10px;
}

img {
  border-radius: 50%;
}

.verification-status {
  margin-top: 15px;
  font-size: 1.1rem;
}

.fas.fa-check-circle {
  font-size: 1.2rem;
  margin-left: 5px;
}

.fas.fa-times-circle {
  font-size: 1.2rem;
  margin-left: 5px;
}

button {
  margin-top: 20px;
}
</style>
