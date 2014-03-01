//
//  Destination.h
//  OurFarm_ios
//
//  景点/农家院明细介绍
//
//  Created by 李 凤勇 on 13-5-30.
//  Copyright (c) 2013年 FarmHome. All rights reserved.
//

#import "Destination.h"
#import "Summary.h"
#import "DestinationInLocalDB.h"
#import "FarmHomeDB.h"

#import "AFAppDotNetAPIClient.h"
#import "AFJSONRequestOperation.h"
#import "SBJson.h"
#import "SBJsonParser.h"
#import "AFHTTPClient.h"

@implementation Destination

- (id)initWithAttributes:(NSDictionary *)attributes {
    self = [super init];
    if (!self) {
        return nil;
    }
    /*
    {
        bike = 0;
        bus = 0;
        car = "5357";
        classicFlag = 0;
        introduction = "U3002";
        labels = "a7";
        mapPic = "http://maps.googleapis.com/maps/api/staticmap?center=39.654892,115.74046&zoom=12&size=400x400&sensor=false";
        otherContact = 0;
        pics =     (
                    "http://farmhome.b0.upaiyun.com/www.jj667.com/fangshan/xianqidong/xianqidong%20(6).jpg",
                    "http://farmhome.b0.upaiyun.com/www.jj667.com/fangshan/xianqidong/xianqidong%20(9).jpg",
                    "http://farmhome.b0.upaiyun.com/www.jj667.com/fangshan/xianqidong/xianqidong%20(10).jpg"
                    );
        picsVilla =     (
                         {
                             content = "";
                             pic = "http://farmhome.b0.upaiyun.com/www.jj667.com/yesanpo/yesanpo%20(9).jpg";
                             type = 4;
                         }
                         );
        preferentialInfo = 0;
        region = "\U623f\U5c71\U533a";
        regionCode = 110111;
        summary =     {
            address = "\U5317\U4eac\U5e02\U623f\U5c71\U533a\U5f20\U574a\U9547\U4e1c\U5173\U4e0a\U6751";
            characteristic = "\U6eb6\U6d1e";
            destinationId = 2;
            distance = 0;
            hot = 6;
            lat = "39.65489959716797";
            lng = "115.73999786376953";
            name = "\U4ed9\U6816\U6d1e";
            phone = "";
            pic = "http://farmhome.b0.upaiyun.com/www.jj667.com/fangshan/xianqidong/xianqidong%20(7).jpg!v1.0";
            price = 45;
            priceInfo = "\U95e8\U7968\Uff1a45RMB";
            score = 4;
            tel = "010-61336228";
            type = 1;
        };
    }
    */
    //contanst
    //_destinationId = [attributes valueForKeyPath:@"destinationId"];
    Summary *sum = [[Summary alloc] init];
    sum.pic = [[attributes valueForKeyPath:@"summary"] valueForKey:@"pic"];
    sum.name = [[attributes valueForKeyPath:@"summary"] valueForKey:@"name"];
    sum.score = [[attributes valueForKeyPath:@"summary"] valueForKey:@"score"];
    sum.priceInfo = [[attributes valueForKeyPath:@"summary"] valueForKey:@"priceInfo"];
    sum.address = [[attributes valueForKeyPath:@"summary"] valueForKey:@"address"];
    sum.tel = [[attributes valueForKeyPath:@"summary"] valueForKey:@"tel"];
    sum.lat = [[attributes valueForKeyPath:@"summary"] valueForKey:@"lat"];
    sum.lng = [[attributes valueForKeyPath:@"summary"] valueForKey:@"lng"];
    sum.destinationId = [[attributes valueForKeyPath:@"summary"] valueForKey:@"destinationId"];
    
    _summary = sum;
    
    _preferentialInfo = [attributes valueForKeyPath:@"preferentialInfo"];
    _pics = [attributes valueForKeyPath:@"pics"];
    _introduction = [attributes valueForKey:@"introduction"];
    
    return self;
}

#pragma mark - get nearby概要信息

+ (void)getDetailWithBlock:(void (^)(Destination *destination, NSDictionary *json, NSError *error))block
                 requestParameter:param {
    
    [AFJSONRequestOperation addAcceptableContentTypes:[NSSet setWithObject:@"text/html"]];
    
    [[AFAppDotNetAPIClient sharedClient] postPath:@"web/Details.php" parameters:param success:^(AFHTTPRequestOperation *operation, id JSON) {
        
        NSLog(@"JSON: %@", JSON);
        
        Destination *destination = [[Destination alloc] initWithAttributes:JSON];
        if (block) {
            block(destination, JSON, nil);
        }
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"%@", operation.responseString);
        if (block) {
            block([[Destination alloc] init], nil, error);
        }
    }];
    
}

#pragma mark - 收藏或者行程
//获取我的收藏或者行程
+ (void)getMyListWithBlock:(void (^)(NSArray *summaryArray, NSError *error))block
                    myType:(int) type {
    NSMutableArray *deslist = nil;
    //收藏
    if(type == 1) {
        deslist = [FarmHomeDB findAllFavorite];
    } else if (type == 2) {//行程
        deslist = [FarmHomeDB findAllThisTrip];
    }
    
    NSLog(@"JSON: %@", deslist);
    
    NSMutableArray *mutableSummary = [[NSMutableArray alloc] init];
    
    for (DestinationInLocalDB *destinationInDB in deslist) {
        NSString *json = [destinationInDB json];
        NSDictionary *des = [NSJSONSerialization JSONObjectWithData:[json dataUsingEncoding:NSUTF8StringEncoding]
                                                            options:NSJSONReadingMutableContainers
                                                              error:nil];
        
        Summary *summary = [[Summary alloc] initWithAttributes:[des valueForKey:@"summary"]];
        [mutableSummary addObject:summary];
    }
    
    if (block) {
        block([NSArray arrayWithArray:mutableSummary], nil);
    }

}


@end
