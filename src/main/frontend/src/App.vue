<script>
import Navigation from './components/navigation/Navigation.vue'
import ErrorModal from './components/ErrorModal.vue'
import store from "@/store";

export default {
  name: 'App',
  components: {
    Navigation,
    ErrorModal
  },
  computed: {
    system() {
      return this.$store.state.config?.system || {};
    },
    point() {
      return this.$store.state.point;
    },
    dateVerification() {
      return this.$store.state.verification.dateInspection
    },
    serialNumber() {
      return this.$store.state.verification.serialNumberTerminal
    },
  },
}
</script>

<template>
  <navigation/>
  <error-modal/>
  <transition name="fade">
    <router-view></router-view>
  </transition>
  <div class="footer">
    <div class="footer__point-version">
      {{ point || 'Незвестный пункт выпуска' }}
      <span>Версия: 2.10.0</span>
    </div>
  </div>
  <div class="footer__serial-number_date-notification">
    <div>
      <span class="date-notification">{{ dateVerification || '' }}</span>
    </div>
    <span class="serial-number">{{ (serialNumber ? 'S/N ' + serialNumber : '') }}</span>
  </div>
</template>

<style>
</style>
