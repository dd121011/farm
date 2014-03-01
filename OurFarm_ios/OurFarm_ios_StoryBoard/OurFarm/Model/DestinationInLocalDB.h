//
//  DestinationInLocalDB.h
//  OurFarm_ios
//
//  数据库中存储的景点对象
//
//  Created by 李 凤勇 on 13-5-31.
//  Copyright (c) 2013年 FarmHome. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface DestinationInLocalDB : NSObject

    @property long destination_id;//景点Id
    @property (nonatomic, copy) NSString *json;//json串，解析后对应Destination对象
    @property BOOL cache;//是否缓存
    @property double lat;//纬度
    @property double lng;//经度
    @property int type;//类型

@end
