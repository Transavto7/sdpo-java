<script>
import PrintLogin from "@/pages/print/PrintLogin";
import PrintIndex from "@/pages/print/PrintIndex";
import Loader from "@/components/common/Loader";
import {getInspections, printInspection} from '@/helpers/api';


export default {
  components: {Loader, PrintIndex, PrintLogin},
  data() {
    return {
      driver: null,
      driverId: null,
      inspection: {},
      inspections: {},
      print: false,
      errorAuthentication: false,
      loading: false,
      hasQuery : false,
    }
  },
  methods: {
    async confirmPrint() {
      console.log('print')
      await printInspection(this.inspection)
      console.log('stop print')
      this.$router.push('/');

    },
    printQuery(inspectionAttr) {
      console.log(inspectionAttr)
      this.hasQuery = true;
      this.inspection = inspectionAttr
    },
    async setDriver(driver) {
      this.driver = driver;
      this.driverId = driver.hash_id
      this.loading = true
      this.inspections =  await getInspections(driver.hash_id);
      this.loading = false;
    },
    setDriverIdRequest(driver) {
      this.driverIdRequest = driver;
      if (this.driverIdRequest.hash_id === this.driver.hash_id) {
        this.confirmPrint()
      } else {
        this.errorAuthentication = true
      }
    }
  },
  computed: {
    login() {
      return this.driverId !== null
    },
  },
}
</script>

<template>
  <print-login v-if="!this.login" @success-auth="(driver) => setDriver(driver)"/>
  <print-index v-if="this.login && !this.hasQuery"
               @print="(inspectionAttr) => printQuery(inspectionAttr)"
               v-model:inspections="inspections"
               v-model:driver="driver"
  />
    <print-login v-if="this.login && this.hasQuery" @success-auth="(driver) => setDriverIdRequest(driver)"
  />
  <!--//todo доавить модалку с ожиданием печати-->
  <!--//todo добавить обработку ошибок-->
  <loader v-model:loading="loading"/>
</template>
