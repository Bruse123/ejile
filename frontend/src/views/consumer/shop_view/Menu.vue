<template>
  <div>

    <div>
      <!--        顶部工具-->
      <el-dropdown>
        <span class="el-dropdown-link">
          {{ sortType }}<i class="el-icon-arrow-down el-icon--right" />
        </span>
        <el-dropdown-menu slot="dropdown">
          <!--        <el-dropdown-item>默认</el-dropdown-item>-->
          <el-dropdown-item @click.native="sortByPrice">按价格</el-dropdown-item>
          <el-dropdown-item @click.native="sortByCnt">按热门</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>

      <el-radio-group v-model="radio" fill="#008836">
        <el-radio-button label="全部" class="radio" @click.native="dataInPage = goodsList" />
        <el-radio-button label="主食" @click.native="dataInPage = goodsList.filter(data => ('主食' === data.type))" />
        <el-radio-button label="小吃" @click.native="dataInPage = goodsList.filter(data => ('小吃' === data.type))" />
        <el-radio-button label="饮料" @click.native="dataInPage = goodsList.filter(data => ('饮料' === data.type))" />
      </el-radio-group>
      <el-autocomplete
        v-model="input"
        class="input inline-input"
        :fetch-suggestions="querySearch"
        :trigger-on-focus="false"
        placeholder="搜索商品名称"
        style="width:300px"
        @select="handleSelect"
      />
      <el-button icon="el-icon-search" class="search" size="small" @click="searchGoods">搜索</el-button>
      <!--  购物车  -->
      <el-popover
        v-model="visible"
        placement="bottom"
        width="400"
        trigger="manual"
      >
        <!-- TODO -->
        <CartDialog
          v-if="visible"
          ref="cart"
          :sid="SID"
          @Buy="sonBuy"
          @Add="sonAddCnt"
          @Sub="sonSubCnt"
          @Delete="sonDelete"
          @Change="sonChange"
        />
        <el-button
          slot="reference"
          size="small"
          icon="el-icon-shopping-cart-full"
          type="success"
          @click="visible = !visible"
        >
          查看购物车
        </el-button>
      </el-popover>
    </div>

    <!--      main-->
    <el-col v-for="(o, index) in dataInPage" :key="o.id" :span="5" :offset="index > 0 ? 3 : 0" class="box-card">
      <el-card :body-style="{ padding: '0px' }" shadow="hover" class="card">
        <el-container>
          <el-aside width="180px">
            <div class="header-style">
              <img v-if="o.pictureUrl === '' || o.pictureUrl === null" :src="require('@/assets/img/default.jpg')" class="food-image">
              <img v-else :src="o.pictureUrl" class="food-image">
            </div>
          </el-aside>
          <el-container>
            <el-header>
              <span style="float: left; padding: 18px 0">{{ o.name }}</span>
              <el-button
                style="float: right; padding: 20px 0"
                type="text"
                @click="showBuyCard(o.id, index)"
              >选规格</el-button>
            </el-header>
            <el-main>
              <span style="float: left;color:#008836; font-weight: bolder;">￥{{ o.price }}</span>
              <el-button
                v-show="getMapCnt(o.id) > 0"
                type="danger"
                round
                size="mini"
                style="float: right;"
                @click="showBuyCard(o.id, index)"
              >{{ getMapCnt(o.id) }}</el-button>
            </el-main>
            <el-footer />

          </el-container>
        </el-container>
      </el-card>
    </el-col>

    <!--      购物栏      -->
    <el-dialog :visible.sync="dialogFormVisible" center>
      <!--商品信息-->
      <GoodsCard :value="goodsList[nowEditIndex]" />
      <el-divider />
      <!--你的购物车-->

      <!--底部操作-->
      <div slot="footer" class="dialog-footer">
        <!--    选择数量为零或不为零    -->
        <el-button v-if="selectedNum === 0" size="mini" type="warning" @click="addToCart(goodsList[nowEditIndex].id)">+加入购物车</el-button>
        <template v-else>
          <el-button type="danger" icon="el-icon-minus" circle size="mini" @click="subCnt(goodsList[nowEditIndex].id)" />
          <el-tag type="info" style="width:100px;">{{ selectedNum }}</el-tag>
          <el-button
            type="success"
            icon="el-icon-plus"
            circle
            size="mini"
            style="background-color: #EBB563; border-color: #EBB563"
            @click="addCnt(goodsList[nowEditIndex].id)"
          />
          <el-button
            v-show="selectedNum > 0"
            type="primary"
            size="mini"
            class="dia-btn"
            @click="submit"
          >确定</el-button>
          <el-button
            v-show="selectedNum > 0"
            type="primary"
            size="mini"
            class="dia-btn"
            @click="buy(goodsList[nowEditIndex].id, new Date().Format('yyyy-MM-dd hh:mm:ss'))"
          >直接购买</el-button>
          <el-button size="mini" style="float:right" type="success" @click="lookCart"><i class="el-icon-shopping-cart-1"> 购物车</i></el-button>
        </template>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import GoodsCard from '@/components/card/GoodsCard'
import CartDialog from '@/components/CartDialog'

export default {
  name: 'Menu',
  components: { GoodsCard, CartDialog },
  props: ['sid'],
  data() {
    return {
      SID: 1,

      // 对话框
      formLabelWidth: '70px',
      dialogFormVisible: false,
      nowEditIndex: 0,
      selectedNum: 0,
      selectedNumMap: {},

      // 输入框
      radio: '全部',
      input: '',

      // 数据
      sortType: '默认',
      dataInPage: [],
      goodsList: [
        {
          buyCount: 331,
          desc: '好康的，也是好吃的',
          id: 3,
          name: '炒面',
          value: '炒面',
          pictureUrl: 'http://localhost:8080/ejile/upload/201907011702352.jpg',
          praise: 0,
          price: 200,
          sid: 1,
          stock: 1,
          type: '主食'
        }
      ],
      visible: false
    }
  },
  watch: {
    'sid'() {
      // console.log(this.$route.params)
      this.SID = this.sid
      this.refreshPage()
    }
  },
  created() {
    // console.log(this.$route.params)
    this.SID = this.sid
    this.refreshPage()
  },
  methods: {
    // @[我们前端写的，不知道有什么用]
    cardMessage(val) {
      this.card = val
    },
    // 搜索
    searchGoods() {
      // console.log(this.radio)
      this.dataInPage = this.goodsList.filter(data =>
        (this.radio === '全部' || data.type === this.radio) &&
          (!this.input || data.name.toLowerCase().includes(this.input.toLowerCase())))
    },

    // 根据 SID 重新刷新页面 获取所有商品
    refreshPage() {
      // FIXME: 购物信息, 这里要先渲染，才能正确加载画面
      this.$get('/consumer/getShoppingCartByCid', {
        CID: this.$db.get('USER_ID')
      }).then(res => {
        const data = res.data.data
        for (let i = 0, len = data.length; i < len; i++) {
          this.selectedNumMap[data[i].gid] = data[i].num // 映射
        }
      }).catch(err => console.log(err))

      // 商品信息
      this.$get('/shop/getAllGoodsBySid', {
        SID: this.SID
      }).then(res => {
        // console.log(res.data)
        this.goodsList = res.data.data
        for (let i = 0, len = this.goodsList.length; i < len; i++) {
          this.goodsList[i].value = this.goodsList[i].name
        }
        this.dataInPage = this.goodsList
        this.radio = '全部'
      }).catch(err => console.log(err))
    },
    // 购买 卡片
    showBuyCard(GID, index) {
      // 先找出当前操作的下标
      index = -1
      for (let i = 0, len = this.goodsList.length; i < len; i++) {
        if (this.goodsList[i].id === GID) {
          index = i
          break
        }
      }
      if (index === -1) {
        this.$message({
          type: 'info',
          message: '真奇怪，出错了.'
        })
        return
      }
      this.nowEditIndex = index
      // 先计算此商品已经挑选数量
      this.$get('/consumer/getShoppingCartByCid', {
        CID: this.$db.get('USER_ID')
      }).then(res => {
        const data = res.data.data
        for (let i = 0, len = data.length; i < len; i++) {
          if (data[i].gid === GID) {
            // 已有此购物
            this.selectedNum = data[i].num
            this.selectedNumMap[GID] = data[i].num
            return
          }
        }
        // 没有此购物
        this.selectedNum = 0
        this.selectedNumMap[GID] = 0
      }).catch(err => console.log(err))
      // 显示对话框
      this.dialogFormVisible = true
      this.visible = false
    },
    // 卡片操作
    // 1 直接购买
    buy(GID, date) {
      this.$confirm('是否确认购买?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 开始购买
        this.$get('/ShoppingCart/confirmItemWithDate', {
          CID: this.$db.get('USER_ID'),
          GID: GID,
          now: date
        }).then(res => {
          this.$message({
            type: 'info',
            message: '购买成功'
          })
          // 挑选数量变为 0
          this.selectedNum = 0
          this.selectedNumMap[GID] = 0
          // FIXME: 广播通知所有商家
          this.$buyNotification.send(JSON.stringify({ SID: this.SID }))
        }).catch(err => console.log(err))
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消购买'
        })
      })
    },
    // 2 查看购物车
    lookCart() {
      this.dialogFormVisible = false
      this.visible = true
    },
    // 3 加入购物
    addToCart(GID) {
      // 没库存了
      if (this.goodsList[this.nowEditIndex].stock === 0) {
        this.$message({
          type: 'info',
          message: '没库存啦，抱歉!'
        })
        return
      }
      //
      this.$get('/ShoppingCart/add', {
        CID: this.$db.get('USER_ID'),
        GID: GID
      }).then(res => {
        // 挑选数量现在为1
        this.selectedNum = 1
        this.selectedNumMap[GID] = 1
      }).catch(err => console.log(err))
    },
    // 计数器
    addCnt(GID) {
      // 库存不够了
      if (this.selectedNum >= this.goodsList[this.nowEditIndex].stock) {
        this.$message({
          type: 'info',
          message: '库存不够了，不要太贪心哟!'
        })
        return
      }
      this.$get('/ShoppingCart/numAdd', {
        CID: this.$db.get('USER_ID'),
        GID: GID
      }).then(res => {
        this.selectedNum += 1
        this.selectedNumMap[GID] += 1
      }).catch(err => console.log(err))
    },
    subCnt(GID) {
      // 为 0 则删掉
      if (this.selectedNum === 1) {
        this.$get('/ShoppingCart/deleteItem', {
          CID: this.$db.get('USER_ID'),
          GID: GID
        }).then(res => {
          this.selectedNum = 0
          this.selectedNumMap[GID] = 0
        }).catch(err => console.log(err))
        return
      }
      //
      this.$get('/ShoppingCart/numSub', {
        CID: this.$db.get('USER_ID'),
        GID: GID
      }).then(res => {
        this.selectedNum -= 1
        this.selectedNumMap[GID] -= 1
      }).catch(err => console.log(err))
    },
    // 确定
    submit() {
      this.dialogFormVisible = false
    },
    // 智能提示
    querySearch(queryString, cb) {
      const goods = this.goodsList
      const results = queryString ? goods.filter(this.createFilter(queryString)) : goods
      // 调用 callback 返回建议列表的数据
      cb(results)
    },
    createFilter(queryString) {
      return (good) => {
        return (good.name.toLowerCase().indexOf(queryString.toLowerCase()) !== -1)
      }
    },
    handleSelect(item) {
      console.log(item)
    },

    // 排序
    sortByPrice() {
      this.sortType = '按价格'
      this.dataInPage = this.dataInPage.sort((x, y) => x.price - y.price)
    },
    sortByCnt() {
      this.sortType = '按热门'
      this.dataInPage = this.dataInPage.sort((x, y) => x.buyCount - y.buyCount)
    },

    // 通信
    sonBuy(items) {
      // console.log("buy")
      for (const item of items) {
        // 视图操作
        this.selectedNumMap[item.gid] = 0
      }
      // 关掉购物车
      this.visible = false
      // FIXME: 广播通知所有商家
      this.$buyNotification.send(JSON.stringify({ SID: this.SID }))
    },
    sonAddCnt(GID) {
      this.selectedNumMap[GID] += 1
    },
    sonSubCnt(GID) {
      this.selectedNumMap[GID] -= 1
    },
    sonChange(row) {
      this.$set(this.selectedNumMap, row.gid, row.num)
      // console.log(this.selectedNumMap)
      // console.log("change")
      this.visible = false
      this.visible = true
    },
    sonDelete(items) {
      // console.log("delete")
      for (const item of items) {
        this.selectedNumMap[item.gid] = 0
      }
      this.visible = false
      this.visible = true
    },

    // sb
    getMapCnt(gid) {
      return this.selectedNumMap[gid]
    }
  }
}
</script>

<style scoped>
  .el-row {
    margin-bottom: 20px;
  }
  .bg-light {
    background: 	#FDF2D4;
  }
  .grid-content {
    border-radius: 4px;
    min-height: 80px;
  }
  .bg-white{
    background: #ededed;
  }
  .shop-name{
    margin:10px 20px 10px;
    padding:0;
    background-color:#FDF2D4;
    font-weight: bold;
    float:left;
  }
  .header{
    margin:0;
    padding:0;
    background-color:#FDF2D4;
    font-weight: bold;
    height:25px;
    border-radius: 4px;
  }
  .message{
    height:75px;
    background-color: #F5F5F5;
  }
  .shop-image{
    width: 150px;
    height:80px;
    margin:10px;
    display: block;
  }
  .shop-info{
    float:left;
    margin-right:40px;
  }
  .food-image {
    width: 150px;
    height:100px;
    margin:10px;
    display: block;
  }
  .box-card {
    width: 400px;
    height: 150px;
    margin:10px auto 10px 70px;
  }
  .card{
    height:130px;
  }
  .card:hover{
    background-color:#F5F5F5;
  }
  .input{
    width:250px;
    margin:auto auto auto 25px;
  }
  .el-pagination.is-background .el-pager li:not(.disabled).active{
    background-color: #008836 !important;
  }
  .el-pagination.is-background .el-pager li:hover{
    color: #008836 !important;
  }
  .input{
    border-color: #008836;
  }
  .search{
    margin:10px;
  }
  .search:hover{
    color:#008836;
    border-color: #008836;
    background-color: rgba(255,205,86,0.1);
  }
  .search:focus{
    color:#008836;
    border-color: #008836;
    background-color: rgba(255,205,86,0.1);
  }
  .trolley:hover{
    color:#008836;
    border-color: #008836;
    background-color: rgba(255,205,86,0.1);
  }
  .trolley:focus{
    color:#008836;
    border-color: #008836;
    background-color: rgba(255,205,86,0.1);
  }
  .dia-btn{
    background-color: #EBB563;
    border-color: #EBB563;
  }
  .el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
  }
  .el-icon-arrow-down {
    font-size: 12px;
  }
</style>
