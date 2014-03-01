//
//  PushMessage.m
//  OurFarm_ios
//
//  系统推送的信息
//
//  Created by 李 凤勇 on 13-5-30.
//  Copyright (c) 2013年 FarmHome. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface PushMessage : NSObject

    @property long idRecommend;//推荐信息id
    @property long destination_id;//内容id，景点/农家乐等内容在后台系统中的唯一标示
    @property (nonatomic, copy) NSString *content;//推荐内容
    @property (nonatomic, copy) NSString *time;//推荐日期
    @property (nonatomic, assign) NSInteger type;//推荐内型：1.首页 2.系统（默认为2）
    @property BOOL isused;//是否为当前推荐

@end
