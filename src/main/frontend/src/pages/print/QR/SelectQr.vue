<script>

export default {
  methods: {
    selectBus() {
      this.$emit('select', 'carId');
    },
    selectDriver() {
      this.$emit('select', 'driverId');
    },
  }
}
</script>

<template>
  <div class="print-selection">
    <div class="tab animate__animated animate__fadeInUp d-1" @click="selectBus()">
      <img width="300" src="@/assets/images/print/busQR.svg">
      <label style="margin-top: 15px;">QR инспекции автомобиля</label>
    </div>
    <div class="tab">
      <img width="300" src="@/assets/images/print/driverQR.svg" @click="selectDriver()">
      <label style="margin-top: 15px;">QR осмотра водителя</label>
    </div>
  </div>
</template>

<style scoped>

</style>