import React from 'react';
import ReactDOM from 'react-dom';
import Map from './Map';

const fetch = require("node-fetch");
const querystring = require('querystring');

const smartphone = "../static/images/smartphone250x250.png";
const like = "../static/images/like.png";
const unlike = "../static/images/unlike.png";
const questionmark = "../static/images/questionmark.png";


class ProductViewSpecs extends React.Component{
  constructor(props){
    super(props)
  }

  getExtraData(product){
    var len = Object.keys(product.extraData).length
    //Array.from(Array(len).keys) creates array [0,1,2,3,...,len] and then we iterate over it
    let result = Array.from(Array(len).keys()).map(i =>
      <tr key={i}>
        <td key={i+5}>{Object.keys(product.extraData)[i] + ":  " + Object.values(product.extraData)[i] }</td>
      </tr>)

    return result
  }


  render(){
    let product = this.props.product;
    let extraData = this.getExtraData(product);
      return(
        <div className="specs">
            <a class='product-name'>{product.name}</a><br />
            <img src={product.image} class='product-image' /><br />
            <table className='specs-table'>
              {extraData}
            </table>
        </div>
      );
  }
}

class ProductViewShopList extends React.Component{

    constructor(props){
        super(props);
        this.state = {
            display: 'none',
            records:null,
            total:null,
            count:6,
            start:0,
            filters:{
              geoDist:-1,
              geoLng:null,
              geoLat:null,
              dateFrom:null,
              dateTo:null,
            },
            filtersDiv:null,
            position:null,
            sliderValue:0,
        };
        this.handleClick = this.handleClick.bind(this);
        this.showFilters = this.showFilters.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
    }

    componentDidMount(){
      navigator.geolocation.getCurrentPosition(position => {
          this.setState({position:position.coords});
          let data = fetch('https://localhost:8765/observatory/api/prices?products='+this.props.productId+"&geoLat="+position.coords.latitude+"&geoLng="+position.coords.longitude+"&geoDist=-1").then((resp)=>{
            resp.json().then((res)=>{
              this.setState({records:res.prices, total:res.total});
            })
          })
      });
      let curDate = new Date();
      curDate.hours = curDate.hours + 2;
      curDate = curDate.toISOString().split('T')[0]
      this.setState({filters:{dateFrom:curDate, dateTo:curDate, geoDist:-1}})
    }

    handleClick(e) {
        const newDisplay = this.state.display == 'none' ? e.target.name : 'none';
        this.setState({display: newDisplay});
    }

    handleSubmit(e) {
      e.preventDefault();
      let finalFilters = this.state.filters;
      if (this.state.position){
        finalFilters.geoLat = this.state.position.latitude
        finalFilters.geoLng = this.state.position.longitude
      }else {
        finalFilters.geoLat = ""
        finalFilters.geoLng = ""
      }
      var arr = [2,5,10,-1];
      let dist = this.state.filters.geoDist == -1 ? -1 : arr[this.state.filters.geoDist]
      finalFilters.geoDist = dist;
      let myQuery = querystring.stringify(finalFilters);
      let data = fetch('https://localhost:8765/observatory/api/prices?products='+this.props.productId+"&"+ myQuery).then((resp)=>{
        resp.json().then((res)=>{
          this.setState({records:res.prices, total:res.total}); 
        })
      }).catch(error => console.error('Error:', error));
    }

    handleChange(e){
      var filterName = event.target.parentElement.id;
      var newObject = this.state.filters;
      newObject[filterName] = event.target.value;
      this.setState({filters:newObject})
    }

    showFilters(){
      let curDate = new Date();
      curDate.hours = curDate.hours + 2;
      curDate = curDate.toISOString().split('T')[0]
      let filters = (
        <div className="filtersDiv">
          <form id="filters" onSubmit={this.handleSubmit}>
            <div id="geoDist" className="DistanceFilter" style={{marginLeft:"10px"}}>
              <label>Απόσταση από το κατάστημα:</label><br /><br />
              <input id="slider" type="range" min="0" max="3" defaultValue="3" onChange={this.handleChange} step="1"/>
              <div id="sliderTags" >
                <h4> 2χλμ </h4>
                <h4> 5χλμ </h4>
                <h4> 10χλμ </h4>
                <h4> Όλα </h4>
              </div>
            </div>
            <div id="dateFrom" className= "DistanceFilter" align="center" style={{marginLeft:"4%"}}>
              <label> Από ημερομηνία: </label> <br /><br />
              <input type="date" defaultValue={curDate}  onChange={this.handleChange} />
            </div>
            <div id="dateTo" className= "DistanceFilter" align="center">
              <label> Έως ημερομηνία: </label> <br /><br />
              <input type="date" defaultValue={curDate} onChange={this.handleChange}/>
            </div>
            <input type="submit" />
          </form>
        </div>
      )
      if (this.state.filtersDiv) this.setState({filtersDiv:null})
      else this.setState({filtersDiv:filters})
    }

    render(){
      if (this.state.records){
      let records = this.state.records;
        const shoplist = records.map((record, i) =>
            <div className='shopbox' key={i}>
                <as className='shopname'>{record.shopName} </as>
                <as className='price'>{record.price} &euro;</as><br/><br/>
                <as style={{float:"left"}}>Απόσταση καταστήματος: {record.distFormatted}χλμ. </as><br/>
                <as style={{float:"left"}}>Αξιοπιστία καταχώρησης: <img src={like} className='like-img'/> <img src={unlike} className='like-img'/><img src={questionmark} className='questionmark-img'/></as><br/><br/>
                <div>
                    <button href='#' className='address' name={'button_' + i} onClick={this.handleClick}>{record.shopAddress}</button><br></br>
                    {this.state.display == 'button_' + i ? <Map lat={record.lat} lng={record.lng}/> : <a></a>}
                </div>
            </div>
          );
        return(
            <div>
                <div className="avail">
                  <h3>Διαθέσιμο στα εξής καταστήματα:</h3>
                  <button className="filtersButton" onClick={this.showFilters} style={{width:"10%"}}> Show Filters </button>
                </div>
                                  {this.state.filtersDiv}
                <div className="myshoplist" align="center">
                  {shoplist}
                </div>
            </div>
        );
      } else {
        return( <div>
                  <button className="filtersButton" onClick={this.showFilters} style={{width:"10%"}}> Show Filters </button>
                  {this.state.filtersDiv}
                  <h3>There are no records for these filters.</h3>
                </div>)
      }
    }
}


export class ProductView extends React.Component {
  constructor(props) {
    super(props);
  }


  render(){
      let product = this.props.product;
        return(
            <div>
              <ProductViewSpecs class='main-product' product={product}/>
              <ProductViewShopList class='main-shops' productId={product.id}/>
            </div>
        );
      }
}
