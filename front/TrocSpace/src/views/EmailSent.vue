<template>
  <div class="email-sent-container">
    <h2>{{ emailSubject }}</h2>
    <p>
      Un email vous a été envoyé à l'adresse suivante : <strong>{{ email }}</strong>.
      Veuillez vérifier votre boîte de réception.
    </p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      email: this.$route.query.email || '',
      motif: this.$route.query.motif || '' // Nouveau paramètre pour le motif
    };
  },
  computed: {
    emailSubject() {
      // Générer un titre basé sur le motif
      switch (this.motif) {
        case 'confirmation':
          return 'Vérification de votre email';
        case 'reset_password':
          return 'Réinitialisation de votre mot de passe';
        default:
          return 'Notification par email';
      }
    }
  }
};
</script>

<style scoped>
.email-sent-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
}
</style>
