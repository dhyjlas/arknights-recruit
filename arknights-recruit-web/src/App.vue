<template>
	<div id="app">
		<router-view v-on:routerpush="routerpush" keep-alive></router-view>
	</div>
</template>

<script>
	export default {
		name: 'app',
		methods: {
			//回调方法页面跳转
			routerpush(e) {
				this.$router.push(e);
			}
		}
	}
</script>
