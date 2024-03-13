<script>
import PrintLogin from "@/pages/print/PrintLogin";
import PrintIndex from "@/pages/print/PrintIndex";
import Loader from "@/components/common/Loader";
import { getInspections } from '@/helpers/api';


export default {
  components: {Loader, PrintIndex, PrintLogin},
  data() {
    return {
      driverId: null,
      inspection: {},
      inspections: {},
      print: false,
      driverIdRequest: null,
      errorAuthentication: false,
      loading: false,
    }
  },
  methods: {
    getInspections() {

    },
    confirmPrint() {
      // todo после подтверждения отправить запрос на печать
    },
    printQuery(inspectionAttr) {
      this.inspection = inspectionAttr
    },
    setDriver(driver) {
      this.driverId = driver
    },
    setDriverIdRequest(driver) {
      this.driverIdRequest = driver;
    }
  },
  watch: {
    driverIdRequest: () => {
      this.errorAuthentication = this.driverId !== this.driverIdRequest;
    },
    driverId: () => {
      this.getInspections()
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
  <print-login v-if="!this.login" @success-auth="(driver) => setDriver(driver)"/>
  <print-index v-if="this.login && !this.hasQuery"
               @print="(inspectionAttr) => printQuery(inspectionAttr)"
               v-model:inspection="inspections"
  />
  <!--  <print-login2 v-if="this.login && this.hasQuery" @success-auth="(driver) => setDriverIdRequest(driver)"-->
  />
  <!--//todo доавить модалку с ожиданием печати-->
  <!--//todo добавить обработку ошибок-->
  <loader v-model:loading="loading"/>
</template>
