<script>

import {getStamps} from "@/helpers/api";

export default {
  name: "Print",
  async mounted() {
    this.stamps = await getStamps();
  },
  data() {
    return {
      stamps: [],
    }
  },
  methods: {
    select(stamp) {
      console.log('select :' + stamp)
    },
    getOrigin(inputJSON) {
      console.log('input ' + inputJSON)
      const convertedJSON = {};
      for (const key in inputJSON) {
        convertedJSON[key] = inputJSON[key][0];
        console.log('промежуток ' + inputJSON[key][0] )
      }
      console.log('output ' + convertedJSON)

      return convertedJSON;
    }
  },

}
</script>

<template>
  <table>
    <thead>
    <tr>
      <th>Заголовок</th>
      <th>Лицензия</th>
      <th></th>
    </tr>
    </thead>
    <tbody class=" animate__animated animate__fadeInUp">
    <tr class=" animate__animated animate__fadeInUp" style="cursor: pointer" v-for="stamp in this.stamps">
      <td >{{ (stamp)[0].stamp_licence }}</td>
      <td>{{ (stamp)[0].stamp_head }}</td>
      <td><a class="btn icon" @click="select(stamp)"> Выбрать </a></td>
    </tr>
    </tbody>
  </table>
</template>


<style scoped>

</style>