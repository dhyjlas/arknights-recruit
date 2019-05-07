import Vue from 'vue'
import {
	Button,
	Card,
	Table,
	Input,
	Col,
	LoadingBar,
	Icon,
	Message,
	Form,
	FormItem,
	ButtonGroup,
	Collapse,
	Panel
} from 'iview'

Vue.component('Button', Button)
Vue.component('Card', Card)
Vue.component('Table', Table)
Vue.component('Input', Input)
Vue.component('Col', Col)
Vue.component('LoadingBar', LoadingBar)
Vue.component('Icon', Icon)
Vue.component('Message', Message)
Vue.component('Form', Form)
Vue.component('FormItem', FormItem)
Vue.component('ButtonGroup', ButtonGroup)
Vue.component('Collapse', Collapse)
Vue.component('Panel', Panel)

Vue.prototype.$loading = LoadingBar;
Vue.prototype.$Message = Message;

import 'iview/dist/styles/iview.css'
