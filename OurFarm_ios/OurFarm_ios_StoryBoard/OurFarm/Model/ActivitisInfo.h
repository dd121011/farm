//
//  ActivitisInfo.m
//  OurFarm_ios
//
//  活动信息
//
//  Created by 李 凤勇 on 13-5-30.
//  Copyright (c) 2013年 FarmHome. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ActivitisInfo : NSObject

    @property long activityId;//后台数据中， 促销的唯一标示
    @property (nonatomic, copy) NSString *name;//活动名称
    @property (nonatomic, copy) NSString *introduction;//活动内容
    @property (nonatomic, copy) NSString *start_time;//促销开始时间，具体到某天，8位字符串
    @property (nonatomic, copy) NSString *end_time;//促销结束时间
    @property float lat;//纬度
    @property float lng;//经度
    @property (nonatomic, copy) NSString *address;//地点
    @property (nonatomic, copy) NSString *pic;//活动的图片
    @property (nonatomic, copy) NSString *tel;//联系电话
    @property (nonatomic, copy) NSString *region_code;//区域编码
    @property (nonatomic, copy) NSString *type;//活动类型
    @property (nonatomic, copy) NSString *www;//官方网站

@end
