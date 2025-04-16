<script>
export default {
  computed: {
    system() {
      return this.$store.state.config?.system || {};
    },
    pageNumber() {
      return this.$route.meta?.number || 0;
    },
  },
}
</script>

<template>
  <div class="step-progress__item" v-if="JSON.parse(system.driver_info)" :class="{active: pageNumber >= 1 }">
    <i class="ri-check-fill"></i>
  </div>
  <div class="step-progress__item" v-if="JSON.parse(system.driver_photo)" :class="{active: pageNumber >= 2 }">
    <i class="ri-check-fill"></i>
  </div>
  <div class="step-progress__item" :class="{active: pageNumber >= 3 }">
    <i class="ri-check-fill"></i>
  </div>
  <div class="step-progress__item" :class="{active: pageNumber >= 4 }">
    <i class="ri-check-fill"></i>
  </div>
  <div class="step-progress__item" :class="{active: pageNumber >= 5 }">
    <i class="ri-check-fill"></i>
  </div>
  <div class="step-progress__item" :class="{active: pageNumber >= 6 }">
    <i class="ri-check-fill"></i>
  </div>
  <div class="step-progress__item" :class="{active: pageNumber >= 7 }">
    <i class="ri-check-fill"></i>
  </div>
  <div class="step-progress__item" :class="{active: pageNumber >= 8 }">
    <i class="ri-check-fill"></i>
  </div>
  <div class="step-progress__item" :class="{active: pageNumber >= 9 }">
    <i class="ri-check-fill"></i>
  </div>
</template>
