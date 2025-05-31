<template>
  <LayoutSidebar>
    <!-- Vérification de l'utilisateur -->
    <div v-if="user.isVerified" class="alert alert-info text-center mt-3">
      <strong>Vous êtes déjà vérifié !</strong>
      <p>Vous ne pouvez pas accéder à cette page.</p>
    </div>

    <div v-else class="selfie-upload-container">
      <h1 class="text-center mt-4">Téléversez une photo selfie</h1>

      <!-- Message rassurant -->
      <div class="alert alert-info text-center mt-3">
        <strong>Vos données sont en sécurité !</strong>
        <p>
          Votre photo selfie et vos informations personnelles ne seront visibles que par l'équipe
          de validation et ne seront jamais partagées avec d'autres utilisateurs. <br />
          Une fois votre compte vérifié, vous ne pourrez plus modifier votre photo selfie.
        </p>
      </div>

      <!-- Section d'exemples -->
      <div class="examples mt-4">
        <h3>Exemples :</h3>
        <div class="row justify-content-center">
          <!-- Exemple Correct -->
          <div class="col-md-3 col-sm-6 mb-4">
            <div class="example">
              <img src="@/assets/images/example-correct.jpg" alt="Exemple Correct" class="img-thumbnail" />
              <p class="text-success text-center mt-2">✅ Correct</p>
            </div>
          </div>

          <!-- Exemples Incorrects -->
          <div class="col-md-3 col-sm-6 mb-4">
            <div class="example">
              <img src="@/assets/images/example-incorrect1.jpg" alt="Exemple Incorrect" class="img-thumbnail" />
              <p class="text-danger text-center mt-2">❌ Flou</p>
            </div>
          </div>
          <div class="col-md-3 col-sm-6 mb-4">
            <div class="example">
              <img src="@/assets/images/example-incorrect2.jpg" alt="Exemple Incorrect" class="img-thumbnail" />
              <p class="text-danger text-center mt-2">❌ Mauvais angle</p>
            </div>
          </div>
          <div class="col-md-3 col-sm-6 mb-4">
            <div class="example">
              <img src="@/assets/images/example-incorrect3.jpg" alt="Exemple Incorrect" class="img-thumbnail" />
              <p class="text-danger text-center mt-2">❌ Mauvaise luminosité</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Formulaire de téléversement -->
      <div class="upload-section mt-5">
        <h3 class="text-center">Sélectionnez votre photo selfie :</h3>
        <div class="form-group mt-3">
          <input
              type="file"
              @change="previewImage"
              accept="image/*"
              class="form-control"
          />
          <small class="text-muted d-block mt-2">
            Assurez-vous que votre visage soit bien centré et clairement visible.
          </small>
        </div>

        <!-- Aperçu de l'image -->
        <div v-if="preview" class="image-preview mt-4 text-center">
          <h4>Aperçu de votre photo :</h4>
          <img :src="preview" alt="Aperçu Selfie" class="img-thumbnail preview-img" />
        </div>

        <!-- Message d'erreur -->
        <div v-if="errorMessage" class="text-danger mt-3 text-center">
          <p>{{ errorMessage }}</p>
        </div>

        <!-- Boutons d'action -->
        <div class="text-center mt-4">
          <button
              class="btn btn-primary me-2"
              @click="submitSelfie"
              :disabled="!selectedFile"
          >
            Soumettre
          </button>
          <button
              class="btn btn-secondary me-2"
              @click="cancelSelection"
              :disabled="!preview"
          >
            Annuler
          </button>

          <button
              v-if="user.selfieCarteIdentite"
              class="btn btn-danger"
              @click="deleteSelfie"
          >
            Supprimer
          </button>
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
      selectedFile: null,
      preview: null,
      errorMessage: "",
      authToken: localStorage.getItem("auth_token"),
      user: {}, // Objet utilisateur
    };
  },
  created() {
    this.checkUserStatus();
  },
  methods: {
    // Vérification du statut de l'utilisateur
    async checkUserStatus() {
      try {
        const response = await axios.get("/user/me", {
          headers: {
            Authorization: `Bearer ${this.authToken}`,
          },
        });
        this.user = response.data;

        // Vérification si l'utilisateur a une selfieCarteIdentite
        if (this.user.selfieCarteIdentite) {
          this.preview = this.user.selfieCarteIdentite; // Affiche la photo si elle existe
        }
      } catch (error) {
        console.error("Erreur de récupération des informations utilisateur:", error);
      }
    },
    previewImage(event) {
      const file = event.target.files[0];

      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          this.preview = e.target.result;
          this.errorMessage = ""; // Réinitialise les messages d'erreur
        };
        reader.readAsDataURL(file);
        this.selectedFile = file;
      } else {
        this.preview = null;
        this.selectedFile = null;
        this.errorMessage = "Aucun fichier sélectionné.";
      }
    },
    async submitSelfie() {
      if (!this.selectedFile) {
        this.errorMessage = "Veuillez sélectionner une photo avant de soumettre.";
        return;
      }

      const formData = new FormData();
      formData.append("selfieCarteIdentite", this.selectedFile);

      try {
        const response = await axios.post("/verification/upload", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${this.authToken}`,
          },
        });

        if (response.status === 200) {
          alert("Votre selfie a été soumis avec succès !");
          this.preview = null; // Réinitialise l'aperçu après soumission
          this.selectedFile = null;
          this.checkUserStatus(); // Recharge les données utilisateur
          this.$router.push("/settings");
        }
      } catch (error) {
        this.errorMessage = "Une erreur s'est produite. Veuillez réessayer.";
      }
    },
    cancelSelection() {
      this.preview = null;
      this.selectedFile = null;
      this.errorMessage = "";
    },
    async deleteSelfie() {
      // Confirmer la suppression avant de procéder
      if (!confirm("Êtes-vous sûr de vouloir supprimer votre selfie ?")) {
        return;
      }

      try {
        const response = await axios.delete(`/verification/delete/selfieCarteIdentite`, {
          headers: {
            Authorization: `Bearer ${this.authToken}`,
          },
        });

        if (response.status === 200) {
          alert("Votre selfie a été supprimé avec succès !");
          this.user.hasSelfie = false; // Met à jour l'état de l'utilisateur
          this.preview = null; // Supprime l'aperçu si affiché
        }
      } catch (error) {
        console.error("Erreur lors de la suppression du selfie :", error);
        this.errorMessage = "Une erreur s'est produite lors de la suppression. Veuillez réessayer.";
      }
    },
  },
};
</script>

<style scoped>
.selfie-upload-container {
  max-width: 800px;
  padding: 2rem;
  background: #fff;
  border-radius: 1rem;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-top: 2em;
}

.examples img {
  max-width: 100%;
  border-radius: 8px;
}

.example {
  margin-bottom: 1rem;
}

.preview-img {
  max-width: 300px;
  margin-top: 1rem;
  border: 2px solid #ddd;
  border-radius: 8px;
}

.image-preview {
  margin-top: 1rem;
  text-align: center;
}

.row {
  display: flex;
  justify-content: center;
}

.col-md-3, .col-sm-6 {
  display: flex;
  justify-content: center;
}

.mb-4 {
  margin-bottom: 1.5rem;
}
.text-center button {
  margin-right: 10px;
}

.text-center button:last-child {
  margin-right: 0; /* Pas d'espace pour le dernier bouton */
}

</style>
