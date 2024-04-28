import http from '@/utils/httpRequest.js'
export function policy(params) {
   return  new Promise((resolve,reject)=>{
        http({
            url: http.adornUrl("/thirdparty/oss/policy"),
            method: "post",
            data: params
        }).then(({ data }) => {
            resolve(data);
        })
    });
}

export function policy2(params) {
    return  new Promise((resolve,reject)=>{
         http({
             url: http.adornUrl("/product/brand/upload/images"),
             method: "post",
             data: params
         }).then(({ data }) => {
             resolve(data);
         })
     });
 }