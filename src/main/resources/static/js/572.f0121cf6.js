"use strict";(self["webpackChunksdpo_frontend"]=self["webpackChunksdpo_frontend"]||[]).push([[572],{2419:function(e,t,s){s.d(t,{NO:function(){return _},QL:function(){return d},TQ:function(){return l},af:function(){return i},o:function(){return o}});var a=s(1857),n=s(8791);async function i(){return await axios.post("device/photo").then((({data:e})=>e)).catch(a.i)}async function l(){return await axios.get("device/video/size").then((({data:e})=>e)).catch(a.i)}async function _(e){return await axios.post("device/media",{driver_id:e}).then((({data:e})=>((0,n.S)(),e))).catch(a.i)}async function d(e){return await axios.post("device/media/stop",{driver_id:e}).then((({data:e})=>e)).catch(a.i)}async function o(){return await axios.post("device/video/test").then((({data:e})=>e)).catch(a.i)}},8791:function(e,t,s){s.d(t,{E:function(){return i},S:function(){return l}});var a=s.p+"media/alert.1cca9e8e.ogg",n=s(7625);function i(){new Audio(a).play()}function l(){let e=15;n.Z.state.waitRecordMedia=!0;let t=setInterval((()=>{e--,console.log("осталось "+e),e<1&&(n.Z.state.waitRecordMedia=!1,clearInterval(t))}),1e3)}},2756:function(e,t,s){s.d(t,{i:function(){return n}});var a=s(1857);async function n(){return await axios.post("device/thermometer").then((({data:e})=>e)).catch(a.i)}},5611:function(e,t,s){s.d(t,{Xi:function(){return _},lm:function(){return l},qb:function(){return i},vX:function(){return n}});var a=s(1857);async function n(){return await axios.post("device/tonometer").then((({data:e})=>e)).catch(a.i)}async function i(){return await axios.post("device/tonometer/disable").then((({data:e})=>e)).catch(a.i)}async function l(e="connect"){return await axios.post("device/tonometer/connect",{status:e}).then((({data:e})=>e)).catch(a.i)}async function _(){return await axios.post("device/scan").then((({data:e})=>e)).catch((e=>{console.log(e)}))}},3487:function(e,t,s){s.r(t),s.d(t,{default:function(){return ra}});var a=s(3396),n=s(7139);const i={class:"admin"},l={class:"admin__tabs"};function _(e,t,s,_,d,o){const c=(0,a.up)("testing"),m=(0,a.up)("tonometer"),r=(0,a.up)("password"),u=(0,a.up)("main-settings"),p=(0,a.up)("print"),h=(0,a.up)("logo");return(0,a.wg)(),(0,a.iD)("div",i,[(0,a._)("div",l,[(0,a._)("button",{class:(0,n.C_)(["admin__tab animate__animated animate__fadeInDown d-1",{active:"main"===d.selected}]),onClick:t[0]||(t[0]=e=>d.selected="main")}," Основные настройки ",2),(0,a._)("button",{class:(0,n.C_)(["admin__tab animate__animated animate__fadeInDown d-2",{active:"password"===d.selected}]),onClick:t[1]||(t[1]=e=>d.selected="password")}," Настройки пароля ",2),(0,a._)("button",{class:(0,n.C_)(["admin__tab animate__animated animate__fadeInDown d-3",{active:"tonometer"===d.selected}]),onClick:t[2]||(t[2]=e=>d.selected="tonometer")}," Настройки тонометра ",2),(0,a._)("button",{class:(0,n.C_)(["admin__tab animate__animated animate__fadeInDown d-4",{active:"print"===d.selected}]),onClick:t[3]||(t[3]=e=>d.selected="print")}," Настройка печати ",2),(0,a._)("button",{class:(0,n.C_)(["admin__tab animate__animated animate__fadeInDown d-3",{active:"logo"===d.selected}]),onClick:t[4]||(t[4]=e=>d.selected="logo")}," Смена логотипа ",2),(0,a._)("button",{class:(0,n.C_)(["admin__tab animate__animated animate__fadeInDown d-4",{active:"testing"===d.selected}]),onClick:t[5]||(t[5]=e=>d.selected="testing")}," Тестирование ",2)]),"testing"===d.selected?((0,a.wg)(),(0,a.j4)(c,{key:0})):(0,a.kq)("",!0),"tonometer"===d.selected?((0,a.wg)(),(0,a.j4)(m,{key:1})):(0,a.kq)("",!0),"password"===d.selected?((0,a.wg)(),(0,a.j4)(r,{key:2})):(0,a.kq)("",!0),"main"===d.selected?((0,a.wg)(),(0,a.j4)(u,{key:3})):(0,a.kq)("",!0),"print"===d.selected?((0,a.wg)(),(0,a.j4)(p,{key:4})):(0,a.kq)("",!0),"logo"===d.selected?((0,a.wg)(),(0,a.j4)(h,{key:5})):(0,a.kq)("",!0)])}const d={class:"admin__testing"},o=["disabled"],c=["disabled"],m=["disabled"],r=["disabled"],u=["disabled"],p=["disabled"],h={key:0,class:"admin__loading animate__animated animate__fadeInUp"},v=(0,a._)("div",{class:"lds-ring"},[(0,a._)("div"),(0,a._)("div"),(0,a._)("div"),(0,a._)("div")],-1),y={key:1,class:"admin__testing-result"},w=["src"],b={key:1,class:"admin__testing-video animate__animated animate__fadeInUp"},g=(0,a._)("video",{autoplay:"autoplay",controls:""},[(0,a._)("source",{src:"http://localhost:8080/video",type:"video/mp4"})],-1),f=[g],k={key:2,class:"admin__testing-temp animate__animated animate__fadeInUp"},U=(0,a._)("span",null,"°C",-1),I={key:3,class:"admin__testing-temp animate__animated animate__fadeInUp"},C={key:4,class:"admin__testing-pressure animate__animated animate__fadeInUp"},S={class:"admin__testing-pressure-item"},x=(0,a._)("span",null,"Систолическое давление",-1),D={class:"admin__testing-pressure-item"},V=(0,a._)("span",null,"Диастолическое давление",-1),q={class:"admin__testing-pressure-item"},z=(0,a._)("span",null,"Пульс",-1);function $(e,t,s,i,l,_){return(0,a.wg)(),(0,a.iD)("div",d,[(0,a._)("button",{disabled:"loading"===l.show,onClick:t[0]||(t[0]=e=>_.photo()),class:"btn blue tab animate__animated animate__fadeInUp"},"Тестовый снимок",8,o),(0,a._)("button",{disabled:"loading"===l.show,onClick:t[1]||(t[1]=e=>_.video()),class:"btn blue tab animate__animated animate__fadeInUp d-1"},"Тестовое видео",8,c),(0,a._)("button",{disabled:"loading"===l.show,onClick:t[2]||(t[2]=e=>_.thermometer()),class:"btn blue tab animate__animated animate__fadeInUp d-2"},"Тест пирометра",8,m),(0,a._)("button",{disabled:"loading"===l.show,onClick:t[3]||(t[3]=e=>_.alcometer()),class:"btn blue tab animate__animated animate__fadeInUp d-3"},"Тест алкометра",8,r),(0,a._)("button",{disabled:"loading"===l.show,onClick:t[4]||(t[4]=e=>_.printer()),class:"btn blue tab animate__animated animate__fadeInUp d-4"},"Тестовая печать",8,u),(0,a._)("button",{disabled:"loading"===l.show,onClick:t[5]||(t[5]=e=>_.tonometer()),class:"btn blue tab animate__animated animate__fadeInUp d-5"},"Тест тонометра",8,p),"loading"===l.show?((0,a.wg)(),(0,a.iD)("div",h,[v,(0,a.Uk)(" Загрузка ")])):((0,a.wg)(),(0,a.iD)("div",y,["image"===l.show?((0,a.wg)(),(0,a.iD)("img",{key:0,class:"animate__animated animate__fadeInUp",src:l.image},null,8,w)):(0,a.kq)("",!0),"video"===l.show?((0,a.wg)(),(0,a.iD)("div",b,f)):(0,a.kq)("",!0),"temp"===l.show?((0,a.wg)(),(0,a.iD)("div",k,[(0,a.Uk)((0,n.zw)(l.temp)+" ",1),U])):(0,a.kq)("",!0),"alcometer"===l.show?((0,a.wg)(),(0,a.iD)("div",I,(0,n.zw)(void 0===l.alcometerPpm?"Не удалось получить результат":l.alcometerPpm+" ‰"),1)):(0,a.kq)("",!0),"pressure"===l.show?((0,a.wg)(),(0,a.iD)("div",C,[(0,a._)("div",S,[x,(0,a.Uk)(" "+(0,n.zw)(l.pressure?.systolic?l.pressure.systolic+" мм. рт.ст":"Результатов нет"),1)]),(0,a._)("div",D,[V,(0,a.Uk)(" "+(0,n.zw)(l.pressure?.diastolic?l.pressure.diastolic+" мм. рт.ст":"Результатов нет"),1)]),(0,a._)("div",q,[z,(0,a.Uk)(" "+(0,n.zw)(l.pressure?.pulse?l.pressure.pulse+" уд/мин":"Результатов нет"),1)])])):(0,a.kq)("",!0)]))])}var L=s(2419),P=s(5611),Z=s(2756),N=s(6700),F=s(4161),j=s(1857);async function M(){return await F.Z.post("device/printer").then((({data:e})=>e)).catch(j.i)}var T={data(){return{show:"",image:null,video:null,temp:null,alcometerPpm:null,pressure:null,timeout:0,loading:!1,interval:null}},methods:{async photo(){this.show="loading",clearInterval(this.interval),this.image=await(0,L.af)(),this.show="",this.image&&setTimeout((()=>{this.show="image"}),2e3)},async video(){this.show="loading",clearInterval(this.interval),this.video=await(0,L.o)(),this.show="",this.video&&(this.show="video")},async thermometer(){this.show="loading",clearInterval(this.interval),this.interval=setInterval((async()=>{this.temp=await(0,Z.i)(),"next"!==this.temp&&(this.show="temp",clearInterval(this.interval))}),1e3)},async alcometer(){this.show="loading",clearInterval(this.interval),this.interval=setInterval((async()=>{const e=await(0,N.pe)();"next"!==e&&(this.alcometerPpm=Number(e)||0,this.show="alcometer",clearInterval(this.interval))}),1e3)},async printer(){this.show="loading",this.alcometerPpm=await M(),this.show=""},async tonometer(){this.show="loading",clearInterval(this.interval),this.interval=setInterval((async()=>{const e=await(0,P.vX)();"next"!==e&&(this.pressure=e,clearInterval(this.interval),this.show="pressure")}),1e3)}}},X=s(89);const B=(0,X.Z)(T,[["render",$]]);var E=B;const H={key:0,class:"admin__loading animate__animated animate__fadeInUp"},R=(0,a._)("div",{class:"lds-ring"},[(0,a._)("div"),(0,a._)("div"),(0,a._)("div"),(0,a._)("div")],-1),Y={key:1,class:"admin__tonometer"},A={class:"admin__tonometer-card animate__animated animate__fadeInUp d-1"},K=(0,a._)("div",{class:"admin__tonometer-card-title"}," Информация ",-1),Q=["disabled"],O={class:"admin__tonometer-devices animate__animated animate__fadeInUp d-3"},W=["disabled","onClick"];function G(e,t,s,i,l,_){return l.result?((0,a.wg)(),(0,a.iD)("div",Y,[(0,a._)("div",A,[K,(0,a._)("p",null,"Подключенный тонометр: "+(0,n.zw)(_.config.main.tonometer_mac||"-"),1),(0,a._)("button",{disabled:l.connecting,class:(0,n.C_)([{red:l.connecting},"btn small blue"]),style:{width:"100%","margin-top":"10px"},onClick:t[0]||(t[0]=(...e)=>_.connect&&_.connect(...e))},"Подключить",10,Q)]),(0,a._)("div",O,[((0,a.wg)(!0),(0,a.iD)(a.HY,null,(0,a.Ko)(l.result?.devices,(e=>((0,a.wg)(),(0,a.iD)("button",{class:"admin__tonometer-device",disabled:e.address===_.config.main.tonometer_mac,key:e.address,onClick:t=>_.save(e.address)},[(0,a.Uk)((0,n.zw)(e.address||"Неизвестный адрес")+" ",1),e.name?((0,a.wg)(),(0,a.iD)(a.HY,{key:0},[(0,a.Uk)(" - "+(0,n.zw)(e.name),1)],64)):(0,a.kq)("",!0)],8,W)))),128))])])):((0,a.wg)(),(0,a.iD)("div",H,[R,(0,a.Uk)(" Загрузка ")]))}var J=s(4457),ee=s(4677),te={data(){return{result:null,scan_task:null,connect_task:null,toast:(0,ee.pm)(),connecting:!1}},computed:{config(){return this.$store.state.config}},methods:{async getDevices(){this.scan_task=setInterval((async()=>{this.result=await(0,P.Xi)()}),1e3)},async save(e){await(0,J.Tc)(e),this.config.main.tonometer_mac=e,this.toast.success("Настройки тонометра сохранены")},async connect(){this.connecting=!0,this.connect_task=setInterval((async()=>{let e=await(0,P.lm)();"set"===e&&(this.connected=!0,this.connecting=!1,this.config.main.tonometer_connect=!0,clearInterval(this.connect_task))}),1e3)}},mounted(){this.getDevices()},unmounted(){clearInterval(this.scan_task),clearInterval(this.connect_task),(0,P.lm)("stop")}};const se=(0,X.Z)(te,[["render",G]]);var ae=se,ne=s(9242);const ie={class:"admin__password"},le={class:"password-form"},_e=(0,a._)("div",{class:"password-form__title animate__animated animate__fadeInDown"}," Введите новый пароль ",-1),de={class:"password-form__input animate__animated animate__fadeIn d-1"},oe={class:"number-buttons",ref:"numbers"},ce=(0,a._)("i",{class:"ri-close-fill"},null,-1),me=[ce],re=(0,a._)("i",{class:"ri-delete-back-line"},null,-1),ue=[re];function pe(e,t,s,n,i,l){return(0,a.wg)(),(0,a.iD)("div",ie,[(0,a._)("div",le,[_e,(0,a._)("div",de,[(0,a.wy)((0,a._)("input",{type:"password","onUpdate:modelValue":t[0]||(t[0]=e=>i.password=e)},null,512),[[ne.nr,i.password]])]),(0,a._)("div",oe,[(0,a._)("button",{class:"number-buttons__item",onClick:t[1]||(t[1]=e=>i.password+="1")},"1"),(0,a._)("button",{class:"number-buttons__item",onClick:t[2]||(t[2]=e=>i.password+="2")},"2"),(0,a._)("button",{class:"number-buttons__item",onClick:t[3]||(t[3]=e=>i.password+="3")},"3"),(0,a._)("button",{class:"number-buttons__item",onClick:t[4]||(t[4]=e=>i.password+="4")},"4"),(0,a._)("button",{class:"number-buttons__item",onClick:t[5]||(t[5]=e=>i.password+="5")},"5"),(0,a._)("button",{class:"number-buttons__item",onClick:t[6]||(t[6]=e=>i.password+="6")},"6"),(0,a._)("button",{class:"number-buttons__item",onClick:t[7]||(t[7]=e=>i.password+="7")},"7"),(0,a._)("button",{class:"number-buttons__item",onClick:t[8]||(t[8]=e=>i.password+="8")},"8"),(0,a._)("button",{class:"number-buttons__item",onClick:t[9]||(t[9]=e=>i.password+="9")},"9"),(0,a._)("button",{class:"number-buttons__item",onClick:t[10]||(t[10]=e=>i.password="")},me),(0,a._)("button",{class:"number-buttons__item",onClick:t[11]||(t[11]=e=>i.password+="0")},"0"),(0,a._)("button",{class:"number-buttons__item",onClick:t[12]||(t[12]=e=>i.password=i.password.slice(0,-1))},ue)],512),i.password.length>3?((0,a.wg)(),(0,a.iD)("button",{key:0,onClick:t[13]||(t[13]=(...e)=>l.save&&l.save(...e)),class:"btn animate__animated animate__fadeInUp"},"Сохранить")):(0,a.kq)("",!0)])])}var he={data(){return{password:"",toast:(0,ee.pm)()}},methods:{async save(){await(0,J.vW)(this.password),this.password="",this.toast.success("Пароль успешно изменен"),this.$store.state.config.password=this.password}},mounted(){this.$refs.numbers.querySelectorAll("button").forEach(((e,t)=>{e.classList.add("animate__animated"),e.classList.add("animate__fadeInUp"),e.style.setProperty("animation-delay",.2+.05*t+"s")}))}};const ve=(0,X.Z)(he,[["render",pe]]);var ye=ve;const we={class:"admin__system"},be={class:"admin__system-card animate__animated animate__fadeInUp d-1"},ge=(0,a._)("div",{class:"admin__system-card__title"}," Основные настройки ",-1),fe={class:"admin__system-card__item"},ke=(0,a._)("span",null,"Информация о водителе",-1),Ue={class:"switch"},Ie=(0,a._)("div",{class:"slider round"},null,-1),Ce={class:"admin__system-card__item"},Se=(0,a._)("span",null,"Проверять номер телефона",-1),xe={class:"switch"},De=(0,a._)("div",{class:"slider round"},null,-1),Ve={class:"admin__system-card__item"},qe=(0,a._)("span",null,"Тип осмотра",-1),ze={class:"switch"},$e=(0,a._)("div",{class:"slider round"},null,-1),Le={class:"admin__system-card__item"},Pe=(0,a._)("span",null,"Вопрос о сне",-1),Ze={class:"switch"},Ne=(0,a._)("div",{class:"slider round"},null,-1),Fe={class:"admin__system-card__item"},je=(0,a._)("span",null,"вопрос о самочувствии",-1),Me={class:"switch"},Te=(0,a._)("div",{class:"slider round"},null,-1),Xe={class:"admin__system-card__item"},Be=(0,a._)("span",null,"Ручной режим",-1),Ee={class:"switch"},He=(0,a._)("div",{class:"slider round"},null,-1),Re={class:"admin__system-card__item"},Ye=(0,a._)("span",null,"Переход в начало",-1),Ae={class:"switch"},Ke=(0,a._)("div",{class:"slider round"},null,-1),Qe={class:"admin__system-card__item"},Oe=(0,a._)("span",null,[(0,a.Uk)("Задержка перед "),(0,a._)("br"),(0,a.Uk)(" повторным прохождением "),(0,a._)("br"),(0,a.Uk)("(в миллисекундах)")],-1),We={class:"admin__system-card animate__animated animate__fadeInUp d-2"},Ge=(0,a._)("div",{class:"admin__system-card__title"}," Алкометр ",-1),Je={class:"admin__system-card__item"},et=(0,a._)("span",null,"Пропуск",-1),tt={class:"switch"},st=(0,a._)("div",{class:"slider round"},null,-1),at={class:"admin__system-card__item"},nt=(0,a._)("span",null,"включен",-1),it={class:"switch"},lt=(0,a._)("div",{class:"slider round"},null,-1),_t={class:"admin__system-card__item"},dt=(0,a._)("span",null,"Быстрый режим",-1),ot={class:"switch"},ct=(0,a._)("div",{class:"slider round"},null,-1),mt={class:"admin__system-card__item"},rt=(0,a._)("span",null,"Количественный замер при положительном тесте",-1),ut={class:"switch"},pt=(0,a._)("div",{class:"slider round"},null,-1),ht={class:"admin__system-card animate__animated animate__fadeInUp d-3"},vt=(0,a._)("div",{class:"admin__system-card__title"}," Тонометр ",-1),yt={class:"admin__system-card__item"},wt=(0,a._)("span",null,"Пропуск",-1),bt={class:"switch"},gt=(0,a._)("div",{class:"slider round"},null,-1),ft={class:"admin__system-card__item"},kt=(0,a._)("span",null,"включен",-1),Ut={class:"switch"},It=(0,a._)("div",{class:"slider round"},null,-1),Ct={class:"admin__system-card animate__animated animate__fadeInUp d-4"},St=(0,a._)("div",{class:"admin__system-card__title"}," Камера ",-1),xt={class:"admin__system-card__item"},Dt=(0,a._)("span",null,"Качество",-1),Vt=["value"],qt={class:"admin__system-card__item"},zt=(0,a._)("span",null,"Видео",-1),$t={class:"switch"},Lt=(0,a._)("div",{class:"slider round"},null,-1),Pt={class:"admin__system-card__item"},Zt=(0,a._)("span",null,"Фото",-1),Nt={class:"switch"},Ft=(0,a._)("div",{class:"slider round"},null,-1),jt={class:"admin__system-card__item"},Mt=(0,a._)("span",null,"Фото водителя",-1),Tt={class:"switch"},Xt=(0,a._)("div",{class:"slider round"},null,-1),Bt={class:"admin__system-card animate__animated animate__fadeInUp d-5"},Et=(0,a._)("div",{class:"admin__system-card__title"}," Принтер ",-1),Ht={class:"admin__system-card__item"},Rt=(0,a._)("span",null,"Печать",-1),Yt={class:"switch"},At=(0,a._)("div",{class:"slider round"},null,-1),Kt={class:"admin__system-card__item"},Qt=(0,a._)("span",null,"Количество",-1),Ot={class:"admin__system-card animate__animated animate__fadeInUp d-6"},Wt=(0,a._)("div",{class:"admin__system-card__title"}," Термометр ",-1),Gt={class:"admin__system-card__item"},Jt=(0,a._)("span",null,"Пропуск",-1),es={class:"switch"},ts=(0,a._)("div",{class:"slider round"},null,-1),ss={class:"admin__system-card__item"},as=(0,a._)("span",null,"включен",-1),ns={class:"switch"},is=(0,a._)("div",{class:"slider round"},null,-1),ls={class:"admin__system-card block animate__animated animate__fadeInUp d-6"},_s={class:"admin__system-card__title"},ds={class:"admin__system-card__item"},os=(0,a._)("span",null,"Адрес",-1),cs={class:"admin__system-card__item"},ms=(0,a._)("span",null,"Токен",-1),rs={class:"admin__system-footer animate__animated animate__fadeInUp d-7"};function us(e,t,s,i,l,_){return(0,a.wg)(),(0,a.iD)("div",we,[(0,a._)("div",be,[ge,(0,a._)("div",fe,[ke,(0,a._)("label",Ue,[(0,a.wy)((0,a._)("input",{type:"checkbox","onUpdate:modelValue":t[0]||(t[0]=e=>_.system.driver_info=e)},null,512),[[ne.e8,_.system.driver_info]]),Ie])]),(0,a._)("div",Ce,[Se,(0,a._)("label",xe,[(0,a.wy)((0,a._)("input",{type:"checkbox","onUpdate:modelValue":t[1]||(t[1]=e=>_.system.check_phone_number=e)},null,512),[[ne.e8,_.system.check_phone_number]]),De])]),(0,a._)("div",Ve,[qe,(0,a._)("label",ze,[(0,a.wy)((0,a._)("input",{type:"checkbox","onUpdate:modelValue":t[2]||(t[2]=e=>_.system.type_ride=e)},null,512),[[ne.e8,_.system.type_ride]]),$e])]),(0,a._)("div",Le,[Pe,(0,a._)("label",Ze,[(0,a.wy)((0,a._)("input",{type:"checkbox","onUpdate:modelValue":t[3]||(t[3]=e=>_.system.question_sleep=e)},null,512),[[ne.e8,_.system.question_sleep]]),Ne])]),(0,a._)("div",Fe,[je,(0,a._)("label",Me,[(0,a.wy)((0,a._)("input",{type:"checkbox","onUpdate:modelValue":t[4]||(t[4]=e=>_.system.question_helth=e)},null,512),[[ne.e8,_.system.question_helth]]),Te])]),(0,a._)("div",Xe,[Be,(0,a._)("label",Ee,[(0,a.wy)((0,a._)("input",{type:"checkbox","onUpdate:modelValue":t[5]||(t[5]=e=>_.system.manual_mode=e)},null,512),[[ne.e8,_.system.manual_mode]]),He])]),(0,a._)("div",Re,[Ye,(0,a._)("label",Ae,[(0,a.wy)((0,a._)("input",{type:"checkbox","onUpdate:modelValue":t[6]||(t[6]=e=>_.system.auto_start=e)},null,512),[[ne.e8,_.system.auto_start]]),Ke])]),(0,a._)("div",Qe,[Oe,(0,a.wy)((0,a._)("input",{min:"1000",class:"medium",type:"number","onUpdate:modelValue":t[7]||(t[7]=e=>_.system.delay_before_retry_inspection=e)},null,512),[[ne.nr,_.system.delay_before_retry_inspection]])])]),(0,a._)("div",We,[Ge,(0,a._)("div",Je,[et,(0,a._)("label",tt,[(0,a.wy)((0,a._)("input",{type:"checkbox","onUpdate:modelValue":t[8]||(t[8]=e=>_.system.alcometer_skip=e)},null,512),[[ne.e8,_.system.alcometer_skip]]),st])]),(0,a._)("div",at,[nt,(0,a._)("label",it,[(0,a.wy)((0,a._)("input",{type:"checkbox","onUpdate:modelValue":t[9]||(t[9]=e=>_.system.alcometer_visible=e)},null,512),[[ne.e8,_.system.alcometer_visible]]),lt])]),(0,a._)("div",_t,[dt,(0,a._)("label",ot,[(0,a.wy)((0,a._)("input",{type:"checkbox","onUpdate:modelValue":t[10]||(t[10]=e=>_.system.alcometer_fast=e)},null,512),[[ne.e8,_.system.alcometer_fast]]),ct])]),(0,a._)("div",mt,[rt,(0,a._)("label",ut,[(0,a.wy)((0,a._)("input",{type:"checkbox","onUpdate:modelValue":t[11]||(t[11]=e=>_.system.alcometer_retry=e)},null,512),[[ne.e8,_.system.alcometer_retry]]),pt])])]),(0,a._)("div",ht,[vt,(0,a._)("div",yt,[wt,(0,a._)("label",bt,[(0,a.wy)((0,a._)("input",{type:"checkbox","onUpdate:modelValue":t[12]||(t[12]=e=>_.system.tonometer_skip=e)},null,512),[[ne.e8,_.system.tonometer_skip]]),gt])]),(0,a._)("div",ft,[kt,(0,a._)("label",Ut,[(0,a.wy)((0,a._)("input",{type:"checkbox","onUpdate:modelValue":t[13]||(t[13]=e=>_.system.tonometer_visible=e)},null,512),[[ne.e8,_.system.tonometer_visible]]),It])])]),(0,a._)("div",Ct,[St,(0,a._)("div",xt,[Dt,(0,a.wy)((0,a._)("select",{"onUpdate:modelValue":t[14]||(t[14]=e=>_.system.camera_dimension=e)},[((0,a.wg)(!0),(0,a.iD)(a.HY,null,(0,a.Ko)(l.videoSizes,(e=>((0,a.wg)(),(0,a.iD)("option",{key:e,value:e},(0,n.zw)(e),9,Vt)))),128))],512),[[ne.bM,_.system.camera_dimension]])]),(0,a._)("div",qt,[zt,(0,a._)("label",$t,[(0,a.wy)((0,a._)("input",{type:"checkbox","onUpdate:modelValue":t[15]||(t[15]=e=>_.system.camera_video=e)},null,512),[[ne.e8,_.system.camera_video]]),Lt])]),(0,a._)("div",Pt,[Zt,(0,a._)("label",Nt,[(0,a.wy)((0,a._)("input",{type:"checkbox","onUpdate:modelValue":t[16]||(t[16]=e=>_.system.camera_photo=e)},null,512),[[ne.e8,_.system.camera_photo]]),Ft])]),(0,a._)("div",jt,[Mt,(0,a._)("label",Tt,[(0,a.wy)((0,a._)("input",{type:"checkbox","onUpdate:modelValue":t[17]||(t[17]=e=>_.system.driver_photo=e)},null,512),[[ne.e8,_.system.driver_photo]]),Xt])])]),(0,a._)("div",Bt,[Et,(0,a._)("div",Ht,[Rt,(0,a._)("label",Yt,[(0,a.wy)((0,a._)("input",{type:"checkbox","onUpdate:modelValue":t[18]||(t[18]=e=>_.system.printer_write=e)},null,512),[[ne.e8,_.system.printer_write]]),At])]),(0,a._)("div",Kt,[Qt,(0,a.wy)((0,a._)("input",{min:"1",class:"small",type:"number","onUpdate:modelValue":t[19]||(t[19]=e=>_.system.print_count=e)},null,512),[[ne.nr,_.system.print_count]])])]),(0,a._)("div",Ot,[Wt,(0,a._)("div",Gt,[Jt,(0,a._)("label",es,[(0,a.wy)((0,a._)("input",{type:"checkbox","onUpdate:modelValue":t[20]||(t[20]=e=>_.system.thermometer_skip=e)},null,512),[[ne.e8,_.system.thermometer_skip]]),ts])]),(0,a._)("div",ss,[as,(0,a._)("label",ns,[(0,a.wy)((0,a._)("input",{type:"checkbox","onUpdate:modelValue":t[21]||(t[21]=e=>_.system.thermometer_visible=e)},null,512),[[ne.e8,_.system.thermometer_visible]]),is])])]),(0,a._)("div",ls,[(0,a._)("div",_s,[(0,a.Uk)(" Настройки API "),(0,a._)("div",{class:(0,n.C_)(["connection-status",{active:_.connection}])},[(0,a._)("span",null,(0,n.zw)(_.connection),1)],2)]),(0,a._)("div",ds,[os,(0,a.wy)((0,a._)("input",{"onUpdate:modelValue":t[22]||(t[22]=e=>_.config.url=e)},null,512),[[ne.nr,_.config.url]])]),(0,a._)("div",cs,[ms,(0,a.wy)((0,a._)("input",{"onUpdate:modelValue":t[23]||(t[23]=e=>_.config.token=e)},null,512),[[ne.nr,_.config.token]])])]),(0,a._)("div",rs,[(0,a._)("button",{onClick:t[24]||(t[24]=(...e)=>_.save&&_.save(...e)),class:"btn blue"},"Сохранить")])])}var ps=s(6509),hs={data(){return{toast:(0,ee.pm)(),videoSizes:[]}},async mounted(){this.videoSizes=await(0,L.TQ)()},methods:{async save(){await(0,J.hR)(this.system),await(0,J.XP)(this.config.url,this.config.token),this.$store.state.point=await(0,ps.F8)(),this.toast.success("Настройки сохранены")}},computed:{system(){return this.$store.state.config?.system||{}},config(){return this.$store.state.config?.main||{}},connection(){return this.$store.state.connection||!1}}};const vs=(0,X.Z)(hs,[["render",us]]);var ys=vs;const ws={class:"admin__logo"},bs={class:"input-file"},gs=(0,a._)("span",null,"Выберите файл",-1),fs={key:0,class:"preview-image"},ks=["src"];function Us(e,t,s,n,i,l){return(0,a.wg)(),(0,a.iD)("div",ws,[(0,a._)("label",bs,[(0,a._)("input",{type:"file",onChange:t[0]||(t[0]=(...e)=>l.onFileChange&&l.onFileChange(...e))},null,32),gs]),l.logo?((0,a.wg)(),(0,a.iD)("div",fs,[(0,a._)("img",{src:l.logo},null,8,ks),(0,a._)("button",{onClick:t[1]||(t[1]=(...e)=>l.deleteLogo&&l.deleteLogo(...e)),class:"btn small"},"Удалить")])):(0,a.kq)("",!0)])}var Is={data(){return{toast:(0,ee.pm)()}},methods:{async onFileChange(e){const t=e.target.files[0],s=await this.getBase64(t);this.$store.state.config.main.logo=s,await(0,ps.Sx)(s),this.toast.success("Логотип успешно установлен")},getBase64(e){return new Promise(((t,s)=>{const a=new FileReader;a.readAsDataURL(e),a.onload=()=>{t(a.result)},a.onerror=e=>{console.log("Error: ",e)}}))},async deleteLogo(){this.$store.state.config.main.logo="",await(0,ps.Sx)(""),this.toast.success("Логотип успешно удален")}},computed:{logo(){return this.$store.state.config?.main?.logo}}};const Cs=(0,X.Z)(Is,[["render",Us]]);var Ss=Cs;const xs={class:"stamp animate__animated animate__fadeInUp"},Ds={class:"stamp__body"},Vs={class:"template"},qs=(0,a._)("div",{class:"template__header"},[(0,a._)("h2",null,"Предпросмотр печати")],-1),zs={class:"template__content"},$s={class:"header"},Ls=(0,a._)("br",null,null,-1),Ps={class:"license"},Zs=(0,a._)("br",null,null,-1),Ns=(0,a._)("span",null,"Иванов Иван",-1),Fs=(0,a._)("br",null,null,-1),js=(0,a._)("span",null,"прошел Предрейсовый/Предсменный",-1),Ms=(0,a._)("br",null,null,-1),Ts=(0,a._)("span",null,"медицинский осмотр",-1),Xs=(0,a._)("br",null,null,-1),Bs=(0,a._)("span",null,"к исполнению трудовых обязаностей ",-1),Es=(0,a._)("br",null,null,-1),Hs=(0,a._)("span",null,"допущен",-1),Rs=(0,a._)("br",null,null,-1),Ys=(0,a._)("span",null,null,-1),As=(0,a._)("br",null,null,-1),Ks=(0,a._)("br",null,null,-1),Qs=(0,a._)("br",null,null,-1),Os=(0,a._)("br",null,null,-1),Ws={key:0,class:"animate__animated animate__fadeInUp d-7",style:{"margin-top":"15px",width:"100%"}},Gs={class:"stampBox"},Js=(0,a._)("div",{class:"stampBox__header"},[(0,a._)("h2",null,"Выбор штампа")],-1),ea={class:"stampBox__content"},ta=["onClick"],sa={class:"header"},aa=(0,a._)("br",null,null,-1),na={class:"license"},ia=(0,a._)("br",null,null,-1);function la(e,t,s,i,l,_){return(0,a.wg)(),(0,a.iD)("div",null,[(0,a._)("div",xs,[(0,a._)("div",Ds,[(0,a._)("div",Vs,[qs,(0,a._)("div",zs,[(0,a._)("span",$s,(0,n.zw)(_.isSelectedStamp?l.selectedStamp.stamp_head:'ООО "Трансавто-7"'),1),Ls,(0,a._)("span",Ps,(0,n.zw)(_.isSelectedStamp?l.selectedStamp.stamp_licence:""),1),Zs,Ns,Fs,js,Ms,Ts,Xs,Bs,Es,Hs,Rs,Ys,As,(0,a._)("span",null,(0,n.zw)(_.date),1),Ks,(0,a._)("span",null,(0,n.zw)(_.medic.name||""),1),Qs,(0,a._)("span",null,"ЭЦП "+(0,n.zw)(_.medic.eds||""),1),Os]),this.isSelectedStamp?((0,a.wg)(),(0,a.iD)("div",Ws,[(0,a._)("button",{onClick:t[0]||(t[0]=(...e)=>_.save&&_.save(...e)),class:"btn blue",style:{width:"100%"}},"Сохранить")])):(0,a.kq)("",!0)]),(0,a._)("div",Gs,[Js,(0,a._)("div",ea,[((0,a.wg)(!0),(0,a.iD)(a.HY,null,(0,a.Ko)(this.stamps,(e=>((0,a.wg)(),(0,a.iD)("div",{class:(0,n.C_)(["stampItem",{selected:_.stampEquals(e[0])}]),onClick:t=>_.selectStamp(e[0])},[(0,a._)("span",sa,(0,n.zw)(e[0].stamp_head),1),aa,(0,a._)("span",na,(0,n.zw)(e[0].stamp_licence),1),ia],10,ta)))),256))])])])])])}var _a={name:"Print",async mounted(){this.selectedStamp=await this.getStampFromLocalStorage(),this.stamps=await(0,ps.t2)()},data(){return{toast:(0,ee.pm)(),stamps:[],selectedStamp:null}},methods:{async getStampFromLocalStorage(){return this.$store.state.config?.main?.selected_stamp??null},selectStamp(e){this.selectedStamp=e},setStampLocalStorage(e){this.$store.state.config.main.selected_stamp={stamp_head:e.stamp_head,stamp_licence:e.stamp_licence}},stampEquals(e){return null!==this.selectedStamp&&e.stamp_licence===this.selectedStamp.stamp_licence},async save(){this.isSelectedStamp?(await(0,ps.XN)(this.selectedStamp),this.setStampLocalStorage(this.selectedStamp),this.toast.success("Настройки сохранены")):this.toast.error("Штамп не выбран")},formatNumeric(e){return e<10?"0"+e:e}},computed:{isSelectedStamp(){return null!==this.selectedStamp},medic(){return this.$store.state.config?.main?.selected_medic||{}},date(){const e=new Date;return e.getFullYear()+"-"+this.formatNumeric(e.getMonth())+"-"+this.formatNumeric(e.getDay())+" "+this.formatNumeric(e.getHours())+":"+this.formatNumeric(e.getMinutes())+":"+this.formatNumeric(e.getSeconds())}}};const da=(0,X.Z)(_a,[["render",la]]);var oa=da,ca={components:{Print:oa,Testing:E,Tonometer:ae,Password:ye,MainSettings:ys,Logo:Ss},mounted(){(0,J.ri)()},data(){return{selected:"main"}}};const ma=(0,X.Z)(ca,[["render",_]]);var ra=ma}}]);
//# sourceMappingURL=572.f0121cf6.js.map