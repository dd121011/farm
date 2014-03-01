//
//  Destination.h
//  OurFarm_ios
//
//  景点/农家院明细介绍
//
//  Created by 李 凤勇 on 13-5-30.
//  Copyright (c) 2013年 FarmHome. All rights reserved.
//

#import <Foundation/Foundation.h>
@class Summary;

@interface Destination : NSObject

    @property (nonatomic, retain) Summary *summary;//概要信息
    @property (nonatomic, copy) NSString *introduction;//内容介绍
    @property (nonatomic, copy) NSString *otherContact;//其他联系方式
    @property (nonatomic, copy) NSString *car;//自驾路线介绍
    @property (nonatomic, copy) NSString *bus;//公共交通路线介绍
    @property (nonatomic, copy) NSString *bike;//骑行路线介绍
    @property BOOL classicFlag;//是否为经典
    @property int regionCode;//区域编码
    @property (nonatomic, copy) NSString *region;//区域名称
    @property (nonatomic, copy) NSString *mapPic;//静态地图
    @property (nonatomic, copy) NSString *preferentialInfo;//优惠信息
    @property (nonatomic, copy) NSString *label;//内容类型，后台系统用于分类
    @property (nonatomic, copy) NSArray *pics;//<String>该景点图片
    @property (nonatomic, retain) NSMutableArray *picsVilla;//<PicsVilla>图文介绍

    // 初始化详细信息
    - (id)initWithDetail:(NSDictionary *)attributes;

    //获取详细信息
    + (void)getDetailWithBlock:(void (^)(Destination *destination, NSDictionary *attributes, NSError *error))block
                 requestParameter:(NSDictionary *)param;

    //获取我的收藏或者行程
    + (void)getMyListWithBlock:(void (^)(NSArray *summaryArray, NSError *error))block
                        myType:(int) type;

@end
