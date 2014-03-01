//
//  Activity.h
//  OurFarm
//
//  Created by tian hao on 13-6-27.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Activity : NSObject

    @property NSNumber *activityId;//id，
    @property (nonatomic, copy) NSString *name;//内容名称
    @property (nonatomic, copy) NSString *introduction;
    @property (nonatomic, copy) NSString *startTime;
    @property (nonatomic, copy) NSString *endTime;
    @property NSNumber *lat;//内容在地图上标点的纬度坐标
    @property NSNumber *lng;//内容在地图上标点的经度坐标
    @property (nonatomic, copy) NSString *address;//内容详细地址
    @property (nonatomic, copy) NSString *pic;//图片URL
    @property (nonatomic, copy) NSString *tel;//座机电话
    @property  NSNumber *type;
    @property (nonatomic, copy) NSString *www;

- (id)initWithAttributes:(NSDictionary *)attributes;

+ (void)getActivityWithBlock:(void (^)(NSArray *posts, NSError *error))block
                 requestParameter:(NSDictionary *)param;

@end
