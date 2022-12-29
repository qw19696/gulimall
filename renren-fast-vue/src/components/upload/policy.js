import http from '@/utils/httpRequest.js'
export function policy() {
   return  new Promise((resolve,reject)=>{
        http({
            url: http.adornUrl("/thirdparty/oss/policy"),
            method: "get",
            params: http.adornParams({})
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