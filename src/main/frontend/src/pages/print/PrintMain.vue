<script>
import PrintLogin2 from "@/pages/print/PrintLogin2";
import PrintIndex from "@/pages/print/PrintIndex";

export default {
  name: 'PrintLogin',
  components: {PrintIndex, PrintLogin2},
  data() {
    return {
      driverId: null,
      inspection: {},
      print: false,
      driverIdRequest : null,
      errorAuthentication : false
    }
  },
  methods: {
    confirmPrint() {
      // todo после подтверждения отправить запрос на печать
    },
    printQuery(inspectionAttr) {
      this.inspection = inspectionAttr
    },
    setDriver(driver) {
      this.driverId = driver
      console.log(this.inspection)
      console.log(this.hasQuery)
      console.log('login ' + this.login)
    },
    setDriverIdRequest(driver) {
      this.driverIdRequest = driver;
    }
  },
  watch: {
    driverIdRequest: () => {
      this.errorAuthentication = this.driverId !== this.driverIdRequest;
    }
  },
  computed: {
    login() {
      return this.driverId !== null
    },
    hasQuery() {
      return false;
      // return this.inspection.inspection_id;
    },
  },
}
</script>

<template>
  <print-login2 v-if="!this.login" @success-auth="(driver) => setDriver(driver)"/>
  <print-index v-if="this.login && !this.hasQuery" @print="(inspectionAttr) => printQuery(inspectionAttr)"/>
<!--  <print-login2 v-if="this.login && this.hasQuery" @success-auth="(driver) => setDriverIdRequest(driver)"-->
  />
  <!--//todo доавить модалку с ожиданием печати-->
  <!--//todo добавить обработку ошибок-->
</template>
