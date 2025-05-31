<template>
  <LayoutSidebar>
    <div class="settings-container">
      <div class="card shadow-lg settings-card">
        <div class="card-body">
          <h3 class="text-center mb-4">Paramètres</h3>

          <!-- Chargement -->
          <div v-if="isLoading" class="spinner mx-auto"></div>

          <!-- Message d'erreur -->
          <div v-else-if="errorMessage" class="text-center text-danger mb-3">
            <p>{{ errorMessage }}</p>
          </div>

          <!-- Contenu des paramètres -->
          <div v-else>
            <!-- Mode Nuit -->
            <div class="settings-item">
              <label for="nightMode" class="form-label">Mode Nuit</label>
              <label class="switch">
                <input
                    type="checkbox"
                    id="nightMode"
                    v-model="nightMode"
                    @change="toggleNightMode"
                />
                <span class="slider"></span>
              </label>
            </div>

            <!-- Demande de vérification -->
            <div class="settings-item">
              <p v-if="!user.isVerified">
                Vérifiez votre compte pour débloquer les fonctionnalités premium :
              </p>
              <button
                  v-if="!user.isVerified"
                  class="btn btn-warning btn-sm"
                  @click="redirectToVerificationPage"
                  style="white-space: nowrap;"
              >
                Vérifier mon compte
              </button>
              <p v-else class="text-success">Compte vérifié</p>
            </div>


          </div>
        </div>
      </div>
    </div>
  </LayoutSidebar>
</template>

<script>
import axios from "axios";
import LayoutSidebar from "@/layouts/LayoutSidebar.vue";

export default {
  components: { LayoutSidebar },
  data() {
    return {
      nightMode: false, // Indique si le mode nuit est activé
      isLoading: false,
      errorMessage: "",
      user: { isVerified: false },
      authToken: localStorage.getItem("auth_token"),
    };
  },
  mounted() {
    if (!this.authToken) {
      window.location.href = "/login";
    } else {
      this.fetchUserData();
    }
    // Vérifier la préférence de mode nuit dans le localStorage
    this.nightMode = localStorage.getItem("nightMode") === "true";
    this.toggleNightMode(); // Applique le mode nuit
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
          this.user = response.data;
          this.nightMode = response.data.nightMode || false;
          this.toggleNightMode(); // Applique immédiatement le mode nuit selon la préférence utilisateur
        }
      } catch (error) {
        if (error.response?.status === 401) {
          window.location.href = "/login";
        } else {
          this.errorMessage =
              "Impossible de récupérer les informations. Veuillez réessayer.";
        }
      } finally {
        this.isLoading = false;
      }
    },
    toggleNightMode() {
      // Applique ou supprime la classe 'night-mode' sur le body
      if (this.nightMode) {
        document.body.classList.add("night-mode");
        localStorage.setItem("nightMode", "true"); // Sauvegarder la préférence
      } else {
        document.body.classList.remove("night-mode");
        localStorage.removeItem("nightMode"); // Supprimer la préférence
      }
    },
    redirectToVerificationPage() {
      window.location.href = "/verification-page"; // Remplacez "/verification" par la route souhaitée
    },
  },
};
</script>

<style scoped>
.settings-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2rem;
}

.settings-card {
  max-width: 600px;
  width: 100%;
  background-color: #f8f9fa;
  border-radius: 1rem;
  padding: 2rem;
}

.settings-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.settings-item label.form-label {
  margin: 0;
  font-size: 1.1rem;
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

.switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 28px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.4s;
  border-radius: 28px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 20px;
  width: 20px;
  border-radius: 50%;
  left: 4px;
  bottom: 4px;
  background-color: white;
  transition: 0.4s;
}

input:checked + .slider {
  background-color: #4caf50;
}

input:checked + .slider:before {
  transform: translateX(22px);
}

/* Mode nuit */
body.night-mode {
  background-color: #121212;
  color: #fff;
}

body.night-mode .settings-card {
  background-color: #333;
  color: #fff;
}
</style>
