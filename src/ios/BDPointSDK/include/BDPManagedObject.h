/****
 *    BDPManagedObject.h
 *
 *    Copyright (C) 2015 Bluedot.  All rights reserved.
 */

#import <Foundation/Foundation.h>

/**
  @brief Defines the methods required by a concrete @ref BDManagedObject sub-class.
*/
@protocol BDPManagedObject <NSObject>

/**
* @param ID A unique string identifier for this managed object; typically a <a href="http://en.wikipedia.org/wiki/Universally_unique_identifier">UUID</a>.
*/
- (id)initWithID: (NSString*)ID;

/**
* <p>Must be implemented by all @ref BDManagedObject sub-classes to copy all state from the given object, to the receiver.</p>
*
* @param object another managed object of the same class, from which state will be copied
*
* @exception NSInvalidArgumentException if object is not of the same class as the receiver
*/
- (void)updateWith: (id<BDPManagedObject>)object;

/**
* <p>A unique string identifier for this managed object; typically a <a href="http://en.wikipedia.org/wiki/Universally_unique_identifier">UUID</a>.</p>
*/
@property (nonatomic) NSString  *ID;

@end
