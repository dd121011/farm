//
//  lineCell.m
//  OurFarm
//
//  Created by tian hao on 13-6-26.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "LineCell.h"
#import "Line.h"
#import "UIImageView+AFNetworking.h"

@implementation LineCell



- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void) setLine:(Line *)line{
    self.name.text = [line valueForKey:@"destinationName"];
    self.content.text = [line valueForKey:@"content"];
    self.characteristic.text = [line valueForKey:@"characteristic"];

    __weak UITableViewCell *_weakcell = self;
    
    if (line.pics.count >0) {
        NSLog (@"index %d has %@", 0, [line.pics objectAtIndex:0]);
        [self.pic1 setImageWithURLRequest:[[NSURLRequest alloc] initWithURL:[NSURL URLWithString:[line.pics objectAtIndex:0]]]
                         placeholderImage:[UIImage imageNamed:@"home_placeholder_pic"]
                                  success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image){
                                      self.pic1.image = image;
                                      [_weakcell setNeedsLayout];
                                  }
                                  failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error){
                                      NSLog(@"Get picture fail in Summary: %@", error);
                                      self.pic1.hidden = YES;
                                  }];
    }
   
    if (line.pics.count >1) {
        NSLog (@"index %d has %@", 1, [line.pics objectAtIndex:1]);
        [self.pic2 setImageWithURLRequest:[[NSURLRequest alloc] initWithURL:[NSURL URLWithString:[line.pics objectAtIndex:1]]]
                         placeholderImage:[UIImage imageNamed:@"home_placeholder_pic"]
                                  success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image){
                                      self.pic2.image = image;
                                      [_weakcell setNeedsLayout];
                                  }
                                  failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error){
                                      NSLog(@"Get picture fail in Summary: %@", error);
                                      self.pic2.hidden = YES;
                                  }];
    }
   
    if (line.pics.count >2) {
        NSLog (@"index %d has %@", 2, [line.pics objectAtIndex:2]);
        [self.pic3 setImageWithURLRequest:[[NSURLRequest alloc] initWithURL:[NSURL URLWithString:[line.pics objectAtIndex:2]]]
                         placeholderImage:[UIImage imageNamed:@"home_placeholder_pic"]
                                  success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image){
                                      self.pic3.image = image;
                                      [_weakcell setNeedsLayout];
                                  }
                                  failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error){
                                      NSLog(@"Get picture fail in Summary: %@", error);
                                      self.pic3.hidden = YES;
                                  }];
    }
    
    if (line.pics.count >3) {
        NSLog (@"index %d has %@", 4, [line.pics objectAtIndex:3]);
        [self.pic4 setImageWithURLRequest:[[NSURLRequest alloc] initWithURL:[NSURL URLWithString:[line.pics objectAtIndex:3]]]
                         placeholderImage:[UIImage imageNamed:@"home_placeholder_pic"]
                                  success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image){
                                      self.pic4.image = image;
                                      [_weakcell setNeedsLayout];
                                  }
                                  failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error){
                                      self.pic4.hidden = YES;
                                      NSLog(@"Get picture fail in Summary: %@", error);
                                  }];
    }
    
    
    
   
}

@end
