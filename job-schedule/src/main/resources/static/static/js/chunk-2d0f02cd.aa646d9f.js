(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-2d0f02cd"],{"9af5":function(e,n,t){"use strict";var o={name:"BsRedirect",beforeCreate:function(){var e=this.$route,n=e.params,t=e.query,o=n.path;this.$router.replace({path:"/"+o,query:t})},render:function(e){return e()}};function r(e,n,t,o,r,i,s,a,d,c){"boolean"!=typeof s&&(d=a,a=s,s=!1);var f,p="function"==typeof t?t.options:t;if(e&&e.render&&(p.render=e.render,p.staticRenderFns=e.staticRenderFns,p._compiled=!0,r&&(p.functional=!0)),o&&(p._scopeId=o),i?(f=function(e){(e=e||this.$vnode&&this.$vnode.ssrContext||this.parent&&this.parent.$vnode&&this.parent.$vnode.ssrContext)||"undefined"==typeof __VUE_SSR_CONTEXT__||(e=__VUE_SSR_CONTEXT__),n&&n.call(this,d(e)),e&&e._registeredComponents&&e._registeredComponents.add(i)},p._ssrRegister=f):n&&(f=s?function(e){n.call(this,c(e,this.$root.$options.shadowRoot))}:function(e){n.call(this,a(e))}),f)if(p.functional){var u=p.render;p.render=function(e,n){return f.call(n),u(e,n)}}else{var v=p.beforeCreate;p.beforeCreate=v?[].concat(v,f):[f]}return t}var i=r,s=o,a=void 0,d=void 0,c=void 0,f=void 0,p=i({},a,s,d,f,c,!1,void 0,void 0,void 0);p.install=function(e){e.component(p.name,p)},e.exports=p}}]);