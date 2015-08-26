/**
* Copyright (c) 2000-present Liferay, Inc. All rights reserved.
*
* This library is free software; you can redistribute it and/or modify it under
* the terms of the GNU Lesser General Public License as published by the Free
* Software Foundation; either version 2.1 of the License, or (at your option)
* any later version.
*
* This library is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
* details.
*/
import UIKit


@objc public protocol UserPortraitScreenletDelegate {

	optional func screenlet(screenlet: UserPortraitScreenlet,
			onUserPortraitResponseImage image: UIImage) -> UIImage

	optional func screenlet(screenlet: UserPortraitScreenlet,
			onUserPortraitError error: NSError)

	optional func screenlet(screenlet: UserPortraitScreenlet,
			onUserPortraitUploaded attributes: [String:AnyObject])

	optional func screenlet(screenlet: UserPortraitScreenlet,
			onUserPortraitUploadError error: NSError)
}


public class UserPortraitScreenlet: BaseScreenlet {

	@IBInspectable public var borderWidth: CGFloat = 1.0 {
		didSet {
			(screenletView as? UserPortraitViewModel)?.borderWidth = self.borderWidth
		}
	}

	@IBInspectable public var borderColor: UIColor? {
		didSet {
			(screenletView as? UserPortraitViewModel)?.borderColor = self.borderColor
		}
	}

	@IBInspectable public var editable: Bool = false {
		didSet {
			(screenletView as? UserPortraitViewModel)?.editable = self.editable
		}
	}

	@IBOutlet public weak var delegate: UserPortraitScreenletDelegate?


	public var viewModel: UserPortraitViewModel {
		return screenletView as! UserPortraitViewModel
	}

	private var loadedUserId: Int64?


	//MARK: BaseScreenlet

	override public func onCreated() {
		super.onCreated()

		viewModel.borderWidth = self.borderWidth
		viewModel.borderColor = self.borderColor
		viewModel.editable = self.editable
	}

	public func loadLoggedUserPortrait() -> Bool {
		let interactor = UserPortraitLoadLoggedUserInteractor(screenlet: self)

		loadedUserId =  SessionContext.currentUserId

		return startInteractor(interactor)
	}

	public func load(#portraitId: Int64, uuid: String, male: Bool = true) -> Bool {
		let interactor = UserPortraitAttributesLoadInteractor(
				screenlet: self,
				portraitId: portraitId,
				uuid: uuid,
				male: male)

		loadedUserId = nil

		return startInteractor(interactor)
	}

	public func load(#userId: Int64) -> Bool {
		let interactor = UserPortraitLoadByUserIdInteractor(
				screenlet: self,
				userId: userId)

		loadedUserId = userId

		return startInteractor(interactor)
	}

	public func load(#companyId: Int64, emailAddress: String) -> Bool {
		let interactor = UserPortraitLoadByEmailAddressInteractor(
				screenlet: self,
				companyId: companyId,
				emailAddress: emailAddress)

		loadedUserId = nil

		return startInteractor(interactor)
	}

	public func load(#companyId: Int64, screenName: String) -> Bool {
		let interactor = UserPortraitLoadByScreenNameInteractor(
				screenlet: self,
				companyId: companyId,
				screenName: screenName)

		loadedUserId = nil

		return startInteractor(interactor)
	}

	override public func createInteractor(#name: String, sender: AnyObject?) -> Interactor? {

		let interactor: UploadUserPortraitInteractor?

		switch name {
		case "upload-portrait":
			let image = sender as! UIImage
			let userId: Int64

			if let loadedUserIdValue = loadedUserId {
				userId = loadedUserIdValue
			}
			else {
				println("ERROR: Can't change the portrait without an userId")

				return nil
			}

			interactor = UploadUserPortraitInteractor(
					screenlet: self,
					userId: userId,
					image: image)

			interactor!.onSuccess = { [weak interactor] in
				self.delegate?.screenlet?(self, onUserPortraitUploaded: interactor!.uploadResult!)
				self.load(userId: userId)
			}

			interactor!.onFailure = {
				self.delegate?.screenlet?(self, onUserPortraitUploadError: $0)
				return
			}

		default:
			interactor = nil
		}

		return interactor
	}

	//MARK: Private methods

	private func startInteractor(interactor: UserPortraitBaseInteractor) -> Bool {
		interactor.onSuccess = {
			if let imageValue = interactor.resultImage {
				let finalImage = self.delegate?.screenlet?(self, onUserPortraitResponseImage: imageValue)

				self.loadedUserId = interactor.resultUserId
				self.setPortraitImage(finalImage ?? imageValue)
			}
			else {
				self.loadedUserId = nil
				self.setPortraitImage(nil)
			}
		}

		interactor.onFailure = {
			delegate?.screenlet?(self, onUserPortraitError: $0)
		}

		return interactor.start()
	}

	private func setPortraitImage(image: UIImage?) {
		viewModel.image = image

		if image == nil {
			let error = NSError.errorWithCause(.AbortedDueToPreconditions)
			delegate?.screenlet?(self, onUserPortraitError: error)
		}
	}

}
