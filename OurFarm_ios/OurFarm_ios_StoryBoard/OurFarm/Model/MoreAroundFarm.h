//
//  MoreAroundFarm.h
//  OurFarm
//
//  周边农家院
//
//  Created by 李 凤勇 on 13-7-1.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <Foundation/Foundation.h>
@class Summary;

@interface MoreAroundFarm : NSObject

@property NSNumber *view_id;//景点ID
@property NSNumber *farmhome_id;//农家乐ID
@property NSNumber *type;//0：系统推荐的农家乐;其他为查询结果
@property NSNumber *distance;//离景点的km
@property (nonatomic, retain) Summary *summary;//概要信息

- (id)initWithAttributes:(NSDictionary *)attributes;
//获取周边农家院
+ (void)getMoreAroundListWithBlock:(void (^)(NSArray *moreAroundFarmArray, NSError *error))block
                  requestParameter:param;

@end
