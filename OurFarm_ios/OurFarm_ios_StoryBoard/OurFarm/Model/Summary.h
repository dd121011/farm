//
//  Summary.h
//  OurFarm_ios
//
//  景点概要信息
//
//  Created by 李 凤勇 on 13-5-30.
//  Copyright (c) 2013年 FarmHome. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Summary : NSObject

    @property NSNumber *destinationId;//内容id，景点/农家乐等内容在后台系统中的唯一标示
    @property (nonatomic, copy) NSString *name;//内容名称
    @property NSNumber *lat;//内容在地图上标点的纬度坐标
    @property NSNumber *lng;//内容在地图上标点的经度坐标
    @property (nonatomic, copy) NSString *address;//内容详细地址
    @property (nonatomic, copy) NSString *pic;//图片URL
    @property (nonatomic, copy) NSString *tel;//座机电话
    @property (nonatomic, copy) NSString *phone;//手机号码
    @property NSNumber *hot;//热度
    @property NSNumber *price;//人均消费/票价
    @property (nonatomic, copy) NSString *priceInfo;//价格信息
    @property NSNumber *score;//平均分数
    @property NSNumber *distance;//离用户距离，单位为km
    @property (nonatomic, copy) NSString *characteristic;//特色，用$分隔
    @property int type;//用于区别4中类型
    @property (readonly, nonatomic, unsafe_unretained) NSURL *avatarImageURL;

    - (id)initWithAttributes:(NSDictionary *)attributes;

    + (void)getSearchSummaryWithBlock:(void (^)(NSArray *posts, NSError *error))block
                 requestParameter:(NSDictionary *)param;

    + (void)getRecommendSummaryWithBlock:(void (^)(NSArray *posts, NSError *error))block
                 requestParameter:(NSDictionary *)param;

    + (void)getNearbySummaryWithBlock:(void (^)(NSArray *posts, NSError *error))block
                     requestParameter:(NSDictionary *)param;

    + (void)getPlansSummaryWithBlock:(void (^)(NSArray *posts, NSError *error))block
                 requestParameter:(NSDictionary *)param;

    + (void)getHotTopSummaryWithBlock:(void (^)(NSArray *posts, NSError *error))block
                requestParameter:(NSDictionary *)param;

    + (void)getAccurateFindSummaryWithBlock:(void (^)(NSArray *posts, NSError *error))block
                       requestParameter:param;



    




@end
