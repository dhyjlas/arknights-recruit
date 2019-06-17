<template>
	<Card style="width:100%;height:100%" :dis-hover="true" :bordered="false">
        <Card :dis-hover="true" >
            <p>明日方舟-公开招募标签组合分析</p>
            <p>以下所有可能招募的结果均建立在招募时间9小时且不被划去标签的情况下</p>
            <p>干员数据来自<a href="http://wiki.joyme.com/arknights" target="_blank">明日方舟wiki</a>，分析结果仅供参考</p>
            <p>如发现错误或有改进建议，可联系邮箱：dhyjlas@163.com</p>
            <p style="margin-bottom: 14px">项目开源地址：<a href="https://github.com/dhyjlas/arknights-recruit" target="_blank">https://github.com/dhyjlas/arknights-recruit</a></p>
        </Card>
		<Form style="margin-top: 20px" :label-width="60">
        <Card :dis-hover="true" >
            <div class='buttonGroup1'>
			<FormItem label="干员职业" prop="occupation">
                <ButtonGroup size="default">
                    <template v-for="occupation in occupations">
                        <Button :id="occupation" :type="isChoice1[occupation]" @click="choice1(occupation)">{{occupation}}</Button>
                    </template>
                </ButtonGroup>
			</FormItem>
			<FormItem label="干员性别" prop="sex">
                <ButtonGroup size="default">
                    <template v-for="sex in sexs">
                        <Button :id="sex" :type="isChoice2[sex]" @click="choice2(sex)">{{sex}}</Button>
                    </template>
                </ButtonGroup>
			</FormItem>
			<FormItem label="干员标签" prop="tag">
                <ButtonGroup size="default">
                    <template v-for="tag in tags">
                        <Button :id="tag" :type="isChoice3[tag]" @click="choice3(tag)">{{tag}}</Button>
                    </template>
                </ButtonGroup>
			</FormItem>
            <FormItem>
                <Button type="primary" class="btn-clear" @click="clear()">清空标签</Button>
            </FormItem>
            </div>
			<!-- <Button type="primary" @click="handleSubmit()" :loading="loading" long>点击获取分析结果</Button> -->
        </Card>
		</Form>
        <Collapse style="margin-top: 20px" v-if="isBegin" v-model="colopen">
            <template v-for="comb in combs">
                <Panel>
                    {{comb.title}}
                    <div slot="content">
                        <Table style="margin: 0px" :show-header="false" :columns="columns" :data="comb.data"></Table>
                    </div>
                </Panel>
            </template>
        </Collapse>
	</Card>
</template>
<script>
	export default {
		data() {
			return {
                colopen: [],
                isSubmit: false,
                isBegin: false,
                timestamp: '',
				player: {
					occupation: {},
                    sex: {},
                    tag: {}
                },
                occupations: ['狙击','术师','先锋','近卫','重装','医疗','辅助','特种'],
                sexs: ['男', '女'],
                tags: ['近战位','远程位','输出','防护','生存','治疗','支援','费用回复','快速复活','群攻','召唤','削弱','减速','控场','位移','爆发','新手','资深干员','高级资深干员'],
                isChoice1: {
                    '狙击': 'default',
                    '术师': 'default',
                    '先锋': 'default',
                    '近卫': 'default',
                    '重装': 'default',
                    '医疗': 'default',
                    '辅助': 'default',
                    '特种': 'default',
                },
                isChoice2: {
                    '男': 'default',
                    '女': 'default',
                },
                isChoice3: {
                    '近战位': 'default',
                    '远程位': 'default',
                    '输出': 'default',
                    '防护': 'default',
                    '生存': 'default',
                    '治疗': 'default',
                    '支援': 'default',
                    '费用回复': 'default',
                    '快速复活': 'default',
                    '群攻': 'default',
                    '召唤': 'default',
                    '削弱': 'default',
                    '减速': 'default',
                    '控场': 'default',
                    '位移': 'default',
                    '爆发': 'default',
                    '新手': 'default',
                    '资深干员': 'default',
                    '高级资深干员': 'default'
                },
                columns: [
                    {
						title: '头像',
                        key: 'pic',
                        render: (h, params) => {
                            return h('div', {
                            attrs: {
                                style: 'width:40px; height:40px;',
                            },
                            }, [
                                h('img', {
                                    attrs: {
                                        src: params.row.pic, style: 'width: 40px;height: 40px;border-radius: 2px;'
                                    },
                                }),
                            ]);
                        },
                        width: 50
					},{
						title: '姓名',
						key: 'name',
                        render: (h, params) => {
                            return h('div', [
                                h('a', {
                                    props: {
                                        type: 'error',
                                        size: 'small'
                                    },
                                    on: {
                                        click: () => {
                                            this.go(params.row.src)
                                        }
                                    }
                                }, params.row.name)
                            ]);
                        },
                        width: 100
					},
					{
						title: '职业',
						key: 'occupation',
                        width: 60
                    },
					{
						title: '星级',
						key: 'level',
                        width: 36
                    },
					{
						title: '性别',
						key: 'sex',
                        width: 36
                    },
					{
						title: '标签',
						key: 'tag',
                        minWidth: 150
                    },
                ],
                combs: [],
                number: 0,
                panelList: [],
                timer: null
			}
		},
		mounted() {
		},
        created (){
            this.timer = setInterval(this.submit, 500);
        },
        beforeDestroy(){
            if(this.timer){
                clearInterval(this.timer);
            }
        },
		methods: {
            clear(){
                const time = (new Date()).getTime();
                this.timestamp = time;
                this.isChoice1 = {
                    '狙击': 'default',
                    '术师': 'default',
                    '先锋': 'default',
                    '近卫': 'default',
                    '重装': 'default',
                    '医疗': 'default',
                    '辅助': 'default',
                    '特种': 'default',
                };
                this.isChoice2 = {
                    '男': 'default',
                    '女': 'default',
                };
                this.isChoice3 = {
                    '近战位': 'default',
                    '远程位': 'default',
                    '输出': 'default',
                    '防护': 'default',
                    '生存': 'default',
                    '治疗': 'default',
                    '支援': 'default',
                    '费用回复': 'default',
                    '快速复活': 'default',
                    '群攻': 'default',
                    '召唤': 'default',
                    '削弱': 'default',
                    '减速': 'default',
                    '控场': 'default',
                    '位移': 'default',
                    '爆发': 'default',
                    '新手': 'default',
                    '资深干员': 'default',
                    '高级资深干员': 'default'
                };
                this.number = 0;
                this.combs = [];
                this.isBegin = false;

            },
            choice1(e){
                if(this.isChoice1[e] == 'default'){
                    if(this.number < 6){
                        this.isChoice1[e] = 'error';
                        this.number ++;
                    }else{
                        this.$Message.error("最多选择6项Tag");
                        return ;
                    }
                }else{
                    this.isChoice1[e] = 'default';
                    this.number --;
                }
                this.isSubmit = true;
            },
            choice2(e){
                if(this.isChoice2[e] == 'default'){
                    if(this.number < 6){
                        this.isChoice2[e] = 'error';
                        this.number ++;
                    }else{
					    this.$Message.error("最多选择6项Tag");
                        return ;
                    }
                }else{
                    this.isChoice2[e] = 'default';
                    this.number --;
                }
                this.isSubmit = true;
            },
            choice3(e){
                if(this.isChoice3[e] == 'default'){
                    if(this.number < 6){
                        this.isChoice3[e] = 'error';
                        this.number ++;
                    }else{
                        this.$Message.error("最多选择6项Tag");
                        return ;
                    }
                }else{
                    this.isChoice3[e] = 'default';
                    this.number --;
                }
                this.isSubmit = true;
            },
            submit(){
                if(this.isSubmit){
                    this.handleSubmit();
                    this.isSubmit = false;
                    this.colopen = [];
                }
            },
			handleSubmit() {
                const time = (new Date()).getTime();
                this.timestamp = time;
                const occupationTags = [];
                const sexTags = []
                const tags = []
                for(var i in this.isChoice1) {
                    if(this.isChoice1[i] == 'error'){
                        occupationTags.push(i);
                    }
                }
                for(var i in this.isChoice2) {
                    if(this.isChoice2[i] == 'error'){
                        sexTags.push(i);
                    }
                }
                for(var i in this.isChoice3) {
                    if(this.isChoice3[i] == 'error'){
                        tags.push(i);
                    }
                }
                this.axios({
                    method: 'get',
                    url: '/comb',
                    params: {
                        occupationTags: occupationTags.join(','),
                        sexTags: sexTags.join(','),
                        tags: tags.join(',')
                    }
                }).then(response => {
                    if(this.timestamp == time){
                        this.combs = [];
                        this.panelList = [];
                        response.data.forEach((item, index) => {
                            const comb = {};
                            const tags = item.tags;
                            if(item.sexTag != ''){
                                tags.unshift(item.sexTag)
                            }
                            if(item.occupationTag != ''){
                                tags.unshift(item.occupationTag);
                            }
                            comb.title = '保底' + item.floors + '星 [' + item.levels[2] + '|' + item.levels[3] + '|'  + item.levels[4] + '|' + item.levels[5] + ']' + '   TAG：' + tags.join('、');
                            comb.data = item.playerList;
                            this.combs.push(comb);
                        });
                        this.isBegin = true;
                    }
                })
            },
            go(e){
                window.open(e);
            }
		}
	}
</script>
<style>
.ivu-form-item {
    margin-bottom: 15px;
}
.ivu-collapse-content>.ivu-collapse-content-box {
    padding-top: 0px;
    padding-bottom: 0px;
}
.ivu-collapse-content {
    padding: 0
}
.ivu-table {
    font-size: 14px;
}
.ivu-table td{
    height: 38px;
}
.ivu-btn-group>.ivu-btn {
    border-radius: 0px;
}
.ivu-card-body {
    padding-bottom: 2px;
}
.ivu-table-cell {
    padding-left: 2px;
    padding-right: 2px;
}
.btn-clear {
    border-radius: 0;
}
</style>
